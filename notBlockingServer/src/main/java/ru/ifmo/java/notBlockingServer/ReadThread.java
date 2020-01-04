package ru.ifmo.java.notBlockingServer;


import ru.ifmo.java.common.protocol.Protocol.Request;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ReadThread implements Runnable {
    private final Selector selector;
    private final WorkerFactory workerFactory;
    private final Map<Channel, ByteBuffer> buffers = new HashMap<>();
    private final Map<Channel, Integer> sizeLastMessage = new HashMap<>();
    private final Map<Channel, ByteBuffer> sizeLastMessageBuffer = new HashMap<>();

    public ReadThread(Selector readSelector, WorkerFactory workerFactory) {
        selector = readSelector;
        this.workerFactory = workerFactory;
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
                    SocketChannel channel = (SocketChannel) key.channel();

                    if (!sizeLastMessage.containsKey(channel)) {
                        readMessageSize(channel);
                    }

                    if (buffers.containsKey(channel)) {
                        readMessage(key, channel, iterator);
                    }
                }
            }
        } catch (IOException | ClosedSelectorException ignored) {
        }
    }

    private void readMessage(SelectionKey key, SocketChannel channel, Iterator<SelectionKey> iterator) throws IOException {
        ByteBuffer byteBuffer = buffers.get(channel);
        channel.read(byteBuffer);

        if (!byteBuffer.hasRemaining()) {
            Request request = Request.parseFrom(byteBuffer.array());
            workerFactory.addWorker(channel, request.getNumberList());
            iterator.remove();
            clear(channel);
        }
    }

    private void clear(SocketChannel channel) {
        buffers.remove(channel);
        sizeLastMessage.remove(channel);
        sizeLastMessageBuffer.remove(channel);
    }

    private void readMessageSize(SocketChannel channel) throws IOException {
        if (sizeLastMessage.containsKey(channel)) {
            return;
        }

        if (!sizeLastMessageBuffer.containsKey(channel)) {
            sizeLastMessageBuffer.put(channel, ByteBuffer.allocate(4));
        }

        ByteBuffer byteBuffer = sizeLastMessageBuffer.get(channel);
        channel.read(byteBuffer);

        if (!byteBuffer.hasRemaining()) {
            byteBuffer.flip();
            int size = byteBuffer.getInt();
            sizeLastMessage.put(channel, size);
            buffers.put(channel, ByteBuffer.allocate(size));
        }
    }
}
