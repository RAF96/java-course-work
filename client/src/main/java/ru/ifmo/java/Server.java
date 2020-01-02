package ru.ifmo.java;

import ru.ifmo.java.common.protocol.Protocol.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Server {
    private final InputStream input;
    private final OutputStream output;

    public Server(Socket socket) throws IOException {
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    public Response send(Request request) throws IOException {
        request.writeDelimitedTo(output);
        return Response.parseDelimitedFrom(input);
    }
}
