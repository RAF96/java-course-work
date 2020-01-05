package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Worker implements Runnable {
    private final InputStream input;
    private final OutputStream output;
    private final Socket socket;

    public Worker(Socket socket) throws IOException {
        input = socket.getInputStream();
        output = socket.getOutputStream();
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed() && !Thread.interrupted()) {
                Request request = Request.parseFrom(OperationWithMessage.readAndUnpackMessage(input));

                List<Integer> list = Sort.sort(request.getNumberList());
                Response response = Response.newBuilder()
                        .addAllNumber(list)
                        .build();

                output.write(OperationWithMessage.packMessage(response.toByteArray()));
            }
        } catch (IOException ignored) {
        }
    }
}
