package ru.ifmo.java.client;

import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.OperationWithMessage;

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
        output.write(OperationWithMessage.packMessage(request.toByteArray()));
        OperationWithMessage.Message message = OperationWithMessage.readAndUnpackMessage(input);
        if (message.endOfInputStream) {
            throw new NotGetResponseFromServer();
        }
        return Response.parseFrom(message.array);
    }

    private static class NotGetResponseFromServer extends RuntimeException {

    }
}
