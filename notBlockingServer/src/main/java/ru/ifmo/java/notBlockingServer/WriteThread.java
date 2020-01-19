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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WriteThread implements Runnable {
    private final Selector selector;

    public WriteThread(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && selector.isOpen()) {
                selector.selectNow();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    Object attachment = key.attachment();
                    ByteBuffer buffer;
                    buffer = getByteBufferFromWriteAttachment(attachment);
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.write(buffer);
                    if (!buffer.hasRemaining()) {
                        key.cancel();
                        iterator.remove();
                    }
                }
            }
        } catch (IOException | ClosedSelectorException ignored) {
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
