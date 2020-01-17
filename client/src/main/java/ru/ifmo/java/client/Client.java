package ru.ifmo.java.client;

import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Client implements Runnable {

    private final Random rand;
    private final Server server;
    private final Socket socket;
    private final ClientsSettings.ClientSettings clientSettings;

    public Client(ClientsSettings clientsSettings) throws IOException {
        this.clientSettings = clientsSettings.clientSettings;
        rand = new Random();
        socket = new Socket(Constant.serverHost, clientsSettings.serverPort);
        server = new Server(socket);
    }

    private static boolean check(List<Integer> listFromClient, List<Integer> listFromServer) {
        Collections.sort(listFromClient);
        return listFromServer.equals(listFromClient);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < clientSettings.numberOfRequestByClient; i++) {
                sendOneRequest();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void sendOneRequest() {
        List<Integer> list = generateRandomValues();
        Request request = Request.newBuilder()
                .addAllNumber(list)
                .build();

        try {
            Response response = server.send(request);
            assert check(list, response.getNumberList());
        } catch (IOException e) {
            e.printStackTrace(); // io problems with one request
        }

        try {
            Thread.sleep(clientSettings.sleepTimeAfterResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> generateRandomValues() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < clientSettings.sizeOfArrayInRequest; i++) {
            list.add(rand.nextInt());
        }
        return list;
    }
}
