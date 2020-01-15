package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WriteTask implements Runnable {
    private final List<Integer> list;
    private final OutputStream outputStream;

    public WriteTask(List<Integer> list, OutputStream outputStream) {
        this.list = list;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        Response response = Response.newBuilder()
                .addAllNumber(list)
                .build();

        try {
            outputStream.write(OperationWithMessage.packMessage(response.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
