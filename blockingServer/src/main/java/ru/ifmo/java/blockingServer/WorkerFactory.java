package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.protocol.Protocol;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class WorkerFactory {

    private final ExecutorService threadPool;
    private final ExecutorService executorServiceWriteThread;
    private final OutputStream outputStream;

    public WorkerFactory(ExecutorService threadPool, ExecutorService executorServiceWriteThread, OutputStream outputStream) {
        this.threadPool = threadPool;
        this.executorServiceWriteThread = executorServiceWriteThread;
        this.outputStream = outputStream;
    }


    public void addWorker(List<Integer> list, Protocol.Response.Timestamps.Builder timestampsBuilder) {
        threadPool.submit(new Worker(list, executorServiceWriteThread, outputStream, timestampsBuilder));
    }
}
