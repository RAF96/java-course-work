package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.notBlockingServer.utils.WriteAttachment;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Worker implements Runnable {
    private final static AtomicInteger inc = new AtomicInteger(0);// MOCK
    private final List<Integer> list;
    private final Selector writeSelector;
    private final Response.Timestamps.Builder timestampBuilder;
    private final ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel;
    private final SocketChannel channel;


    public Worker(List<Integer> list, SocketChannel channel,
                  Selector writeSelector, Response.Timestamps.Builder timestampBuilder,
                  ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel) {
        this.list = list;
        this.channel = channel;
        this.writeSelector = writeSelector;
        this.timestampBuilder = timestampBuilder;
        this.preregisterWriteChannel = preregisterWriteChannel;
    }


    @Override
    public void run() {
        timestampBuilder.setRequestProcessingStart(System.currentTimeMillis());
        List<Integer> sortedList = Sort.sort(list);
        timestampBuilder.setRequestProcessingFinish(System.currentTimeMillis());
        WriteAttachment attachment = new WriteAttachment(sortedList, timestampBuilder);
        preregisterWriteChannel.put(channel, attachment);
    }
}
