package ru.ifmo.java;

import ru.ifmo.java.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Worker implements Runnable{
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
            while (!socket.isClosed()) {
                Request request = Request.parseDelimitedFrom(input);
                List<Integer> list = Sort.sort(request.getNumberList());
                Response response = Response.newBuilder()
                        .addAllNumber(list)
                        .build();

                response.writeDelimitedTo(output);
            }
        } catch (IOException ignored) {
        }
    }
}
