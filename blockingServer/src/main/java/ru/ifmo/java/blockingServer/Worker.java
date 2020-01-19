package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Worker implements Runnable {

    private final List<Integer> list;
    private final ExecutorService executorService;
    private final OutputStream outputStream;
    private final Protocol.Response.Timestamps.Builder timestampsBuilder;

    public Worker(List<Integer> list, ExecutorService executorService, OutputStream outputStream, Protocol.Response.Timestamps.Builder timestampsBuilder) {
        this.list = list;
        this.executorService = executorService;
        this.outputStream = outputStream;
        this.timestampsBuilder = timestampsBuilder;
    }

    @Override
    public void run() {
        timestampsBuilder.setRequestProcessingStart(System.currentTimeMillis());
        List<Integer> result = Sort.sort(list);
        timestampsBuilder.setRequestProcessingFinish(System.currentTimeMillis());
        executorService.submit(new WriteTask(result, outputStream, timestampsBuilder));
    }
}
