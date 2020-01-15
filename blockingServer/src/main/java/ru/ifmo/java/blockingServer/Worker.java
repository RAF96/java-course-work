package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.algorithm.Sort;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Worker implements Runnable {

    private final List<Integer> list;
    private final ExecutorService executorService;
    private final OutputStream outputStream;

    public Worker(List<Integer> list, ExecutorService executorService, OutputStream outputStream) {
        this.list = list;
        this.executorService = executorService;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        List<Integer> result = Sort.sort(list);
        executorService.submit(new WriteTask(result, outputStream));
    }
}
