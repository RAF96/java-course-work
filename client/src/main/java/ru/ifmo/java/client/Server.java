package ru.ifmo.java.client;

import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {
    private final InputStream input;
    private final OutputStream output;

    public Server(Socket socket) throws IOException {
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    // MOCK, refactor code with naming of variables
    public Response send(Request request) throws IOException {
        int serializedSize = request.getSerializedSize();
        byte[] outputArray = ByteBuffer.allocate(4).putInt(serializedSize).array();
        output.write(outputArray);
        request.writeTo(output);

        byte[] inputArray = new byte[4];
        input.read(inputArray);
        int size = ByteBuffer.wrap(inputArray).getInt();
        byte[] inputArrayResponse = new byte[size];
        input.read(inputArrayResponse);
        Response response = Response.parseFrom(inputArrayResponse);
        return response;
    }
}
