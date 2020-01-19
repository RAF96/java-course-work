package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.notBlockingServer.utils.WriteAttachment;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerFactory implements AutoCloseable {

    private final ExecutorService executorService = Executors.newFixedThreadPool(Constant.numberThreadOfServerPool);
    private final Selector writeSelector;
    private final ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel;

    public WorkerFactory(Selector writeSelector, ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel) {
        this.writeSelector = writeSelector;
        this.preregisterWriteChannel = preregisterWriteChannel;
    }

    public void addWorker(SocketChannel socketChannel, List<Integer> list, Protocol.Response.Timestamps.Builder builder) {
        executorService.submit(new Worker(list, socketChannel, writeSelector, builder, preregisterWriteChannel));
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
