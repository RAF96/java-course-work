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

    public Response send(Request request) throws IOException {
        int serializedSize = request.getSerializedSize();
        byte[] outputArray = ByteBuffer.allocate(4).putInt(serializedSize).array();
        output.write(outputArray);
        request.writeTo(output);

        byte[] inputArray = new byte[4];
        input.read(inputArray);
        return Response.parseFrom(input);
    }
}
