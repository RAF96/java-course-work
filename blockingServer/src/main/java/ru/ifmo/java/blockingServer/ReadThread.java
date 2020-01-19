package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.protocol.Protocol;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.utils.OperationWithMessage;

import java.io.IOException;
import java.io.InputStream;

public class ReadThread implements Runnable {
    private final InputStream input;
    private final WorkerFactory workerFactory;

    public ReadThread(WorkerFactory workerFactory, InputStream input) {
        this.workerFactory = workerFactory;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                OperationWithMessage.Message message = OperationWithMessage.readAndUnpackMessage(input);
                if (message.endOfInputStream) return;

                byte[] bytes = message.array;
                Request request = Request.parseFrom(bytes);
                Protocol.Response.Timestamps.Builder timestampsBuilder =
                        Protocol.Response.Timestamps.
                                newBuilder().setClientProcessingStart(System.currentTimeMillis());
                workerFactory.addWorker(request.getNumberList(), timestampsBuilder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
