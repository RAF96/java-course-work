package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.common.utils.OperationWithMessage;
import ru.ifmo.java.notBlockingServer.utils.WriteAttachment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WriteThread implements Runnable {
    private final Selector selector;
    private final ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel;
    private final Map<SocketChannel, ByteBuffer> channelByteBufferHashMap = new HashMap<>();

    public WriteThread(Selector selector,
                       ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel) {
        this.selector = selector;
        this.preregisterWriteChannel = preregisterWriteChannel;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && selector.isOpen()) {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();


                Iterator<Map.Entry<SocketChannel, WriteAttachment>> iterator1 = preregisterWriteChannel.entrySet().iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<SocketChannel, WriteAttachment> next = iterator1.next();
                    SocketChannel channel = next.getKey();
                    WriteAttachment writeAttachment = next.getValue();

                    if (selector.keys().stream().map((x) -> (SocketChannel) x.channel()).anyMatch((x) -> x.equals(channel))) {
                        System.out.print("y != x + 1");
                    }
                    iterator1.remove();
                    SelectionKey register = channel.register(selector, SelectionKey.OP_WRITE, writeAttachment);
                }


                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    SocketChannel channel = (SocketChannel) key.channel();
                    Object attachment = key.attachment();

                    ByteBuffer buffer;
                    if (channelByteBufferHashMap.containsKey(channel)) {
                        buffer = channelByteBufferHashMap.get(channel);
                    } else {
                        buffer = getByteBufferFromWriteAttachment(attachment);
                        channelByteBufferHashMap.put(channel, buffer);
                    }
                    channel.write(buffer);
                    if (!buffer.hasRemaining()) {
                        key.cancel();
                        channelByteBufferHashMap.remove(channel);
                    }
                    iterator.remove();
                }

            }
        } catch (IOException | ClosedSelectorException ignored) {
            ignored.printStackTrace();
        }
    }

    private ByteBuffer getByteBufferFromWriteAttachment(Object object) {
        WriteAttachment attachment;
        attachment = (WriteAttachment) object;
        List<Integer> sortedList = attachment.getSortedList();
        Protocol.Response.Timestamps.Builder timestampBuilder = attachment.getTimestampBuilder();
        timestampBuilder.setClientProcessingFinish(System.currentTimeMillis());

        Protocol.Response response = Protocol.Response.newBuilder()
                .addAllNumber(sortedList)
                .setTimestamps(timestampBuilder.build())
                .build();

        byte[] bytes = OperationWithMessage.packMessage(response.toByteArray());
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length).put(bytes);
        byteBuffer.flip();
        return byteBuffer;
    }
}
