package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Response;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

public class Worker implements Runnable {
    private final List<Integer> list;
    Selector writeSelector;
    SocketChannel channel;

    public Worker(List<Integer> list, SocketChannel channel, Selector writeSelector) {
        this.list = list;
        this.channel = channel;
        this.writeSelector = writeSelector;
    }

    @Override
    public void run() {
        List<Integer> sortedList = Sort.sort(list);
        Response response = Response.newBuilder()
                .addAllNumber(sortedList)
                .build();

        int serializedSize = response.getSerializedSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + serializedSize)
                .putInt(serializedSize).put(response.toByteArray());

        byteBuffer.flip();

        try {
            SelectionKey register = channel.register(writeSelector, SelectionKey.OP_WRITE);
            register.attach(byteBuffer);
        } catch (ClosedChannelException ignored) {
        }
    }
}
