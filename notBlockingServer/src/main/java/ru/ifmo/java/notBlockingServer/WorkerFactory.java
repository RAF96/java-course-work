package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.protocol.Protocol;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkerFactory implements AutoCloseable {

    private final ExecutorService executorService = Executors.newFixedThreadPool(Constant.numberThreadOfServerPool);
    private final Selector writeSelector;

    public WorkerFactory(Selector writeSelector) {
        this.writeSelector = writeSelector;
    }

    public void addWorker(SocketChannel socketChannel, List<Integer> list, Protocol.Response.Timestamps.Builder builder) {
        executorService.submit(new Worker(list, socketChannel, writeSelector, builder));
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
