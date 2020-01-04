package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.algorithm.Sort;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
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

    // MOCK, refactor code with naming of variables
    @Override
    public void run() {
        try {
            while (!socket.isClosed() && !Thread.interrupted()) {
                byte[] inputArray = new byte[4];
                input.read(inputArray);
                int size = ByteBuffer.wrap(inputArray).getInt();
                byte[] inputArrayRequest = new byte[size];
                input.read(inputArrayRequest);
                Request request = Request.parseFrom(inputArrayRequest);

                List<Integer> list = Sort.sort(request.getNumberList());
                Response response = Response.newBuilder()
                        .addAllNumber(list)
                        .build();

                int serializedSize = response.getSerializedSize();
                byte[] outputArray = ByteBuffer.allocate(4).putInt(serializedSize).array();
                output.write(outputArray);
                response.writeTo(output);
            }
        } catch (IOException ignored) {
        }
    }
}
