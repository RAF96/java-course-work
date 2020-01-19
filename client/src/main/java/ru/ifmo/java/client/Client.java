package ru.ifmo.java.client;

import ru.ifmo.java.client.metrics.MetricType;
import ru.ifmo.java.client.metrics.MetricsWriterForOneClient;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.protocol.Protocol.Request;
import ru.ifmo.java.common.protocol.Protocol.Response;
import ru.ifmo.java.common.utils.PairOfTime;

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
    private final ClientsSettings clientsSettings;

    private final PairOfTime requestProcessing = new PairOfTime();
    private final PairOfTime clientProcessing = new PairOfTime();
    private final PairOfTime meanOneRequestByClient = new PairOfTime();

    public Client(ClientsSettings clientsSettings) throws IOException {
        this.clientsSettings = clientsSettings;
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
        meanOneRequestByClient.start = System.currentTimeMillis();
        try {
            for (int i = 0; i < clientsSettings.clientSettings.numberOfRequestByClient; i++) {
                sendOneRequest();
            }
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        meanOneRequestByClient.end = System.currentTimeMillis();

        try {
            writeTimestampByClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTimestampByClient() throws IOException {
        MetricsWriterForOneClient.write(MetricType.MEAN_ONE_REQUEST_BY_CLIENT,
                (double) meanOneRequestByClient.delta() / clientsSettings.numberOfClients);
    }

    private void writeTimestampByRequest() throws IOException {
        MetricsWriterForOneClient.write(MetricType.REQUEST_PROCESSING,
                requestProcessing.delta());

        MetricsWriterForOneClient.write(MetricType.CLIENT_PROCESSING,
                clientProcessing.delta());
    }

    private void sendOneRequest() {
        List<Integer> list = generateRandomValues();
        Request request = Request.newBuilder()
                .addAllNumber(list)
                .build();

        try {
            Response response = server.send(request);
            setTimestampFromServer(response);
            assert check(list, response.getNumberList());
        } catch (IOException e) {
            e.printStackTrace(); // io problems with one request
        }

        try {
            writeTimestampByRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(clientsSettings.clientSettings.sleepTimeAfterResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setTimestampFromServer(Response response) {
        Response.Timestamps timestamps = response.getTimestamps();

        requestProcessing.start = timestamps.getRequestProcessingStart();
        requestProcessing.end = timestamps.getRequestProcessingFinish();

        clientProcessing.start = timestamps.getClientProcessingStart();
        clientProcessing.end = timestamps.getClientProcessingFinish();
    }

    private List<Integer> generateRandomValues() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < clientsSettings.clientSettings.sizeOfArrayInRequest; i++) {
            list.add(rand.nextInt());
        }
        return list;
    }
}
