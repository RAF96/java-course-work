package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;
import ru.ifmo.java.common.utils.PairOfTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Worker implements Runnable {
    private final InputStream input;
    private final OutputStream output;
    private final Socket socket;

    private final PairOfTime requestProcessing = new PairOfTime();
    private final PairOfTime clientProcessing = new PairOfTime();

    public Worker(Socket socket) throws IOException {
        clientProcessing.start = System.currentTimeMillis();
        input = socket.getInputStream();
        output = socket.getOutputStream();
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed() && !Thread.interrupted()) {
                OperationWithMessage.Message message = OperationWithMessage.readAndUnpackMessage(input);
                if (message.endOfInputStream) {
                    socket.close();
                    return;
                }
                Request request = Request.parseFrom(message.array);

                requestProcessing.start = System.currentTimeMillis();
                List<Integer> list = Sort.sort(request.getNumberList());
                requestProcessing.end = System.currentTimeMillis();
                clientProcessing.end = System.currentTimeMillis();

                Response response = Response.newBuilder()
                        .addAllNumber(list)
                        .setTimestamps(getTimestamps())
                        .build();

                output.write(OperationWithMessage.packMessage(response.toByteArray()));
            }
        } catch (IOException ignored) {
        }
    }

    private Response.Timestamps getTimestamps() {
        return Response.Timestamps.newBuilder()
                .setRequestProcessingStart(requestProcessing.start)
                .setRequestProcessingFinish(requestProcessing.end)
                .setClientProcessingStart(clientProcessing.start)
                .setClientProcessingFinish(clientProcessing.end)
                .build();
    }
}
