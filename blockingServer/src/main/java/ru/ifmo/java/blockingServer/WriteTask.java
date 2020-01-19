package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WriteTask implements Runnable {
    private final List<Integer> list;
    private final OutputStream outputStream;
    private final Response.Timestamps.Builder timestampBuilder;

    public WriteTask(List<Integer> list, OutputStream outputStream, Response.Timestamps.Builder timestampBuilder) {
        this.list = list;
        this.outputStream = outputStream;
        this.timestampBuilder = timestampBuilder;
    }

    @Override
    public void run() {
        timestampBuilder.setClientProcessingFinish(System.currentTimeMillis());
        Response response = Response.newBuilder()
                .addAllNumber(list)
                .setTimestamps(timestampBuilder.build())
                .build();

        try {
            outputStream.write(OperationWithMessage.packMessage(response.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
