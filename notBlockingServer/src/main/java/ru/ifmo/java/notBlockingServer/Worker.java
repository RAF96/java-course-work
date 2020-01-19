package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;
import ru.ifmo.java.notBlockingServer.utils.WriteAttachment;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Worker implements Runnable {
    private final List<Integer> list;
    Selector writeSelector;
    private final Response.Timestamps.Builder timestampBuilder;
    SocketChannel channel;

    public Worker(List<Integer> list, SocketChannel channel,
                  Selector writeSelector, Response.Timestamps.Builder timestampBuilder) {
        this.list = list;
        this.channel = channel;
        this.writeSelector = writeSelector;
        this.timestampBuilder = timestampBuilder;
    }


    @Override
    public void run() {
        timestampBuilder.setRequestProcessingStart(System.currentTimeMillis());
        List<Integer> sortedList = Sort.sort(list);
        timestampBuilder.setRequestProcessingFinish(System.currentTimeMillis());

        try {
            WriteAttachment attachment = new WriteAttachment(sortedList, timestampBuilder);
            SelectionKey register = channel.register(writeSelector, SelectionKey.OP_WRITE, attachment);
        } catch (ClosedChannelException ignored) {
        }
    }
}
