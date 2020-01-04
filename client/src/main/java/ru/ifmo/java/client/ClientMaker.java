package ru.ifmo.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientMaker implements Runnable {
    private final ClientsSettings clientsSettings;

    public ClientMaker(ClientsSettings clientsSettings) {
        this.clientsSettings = clientsSettings;
    }

    @Override
    public void run() {
        List<Thread> clients = new ArrayList<>();

        for (int i = 0; i < clientsSettings.numberOfClients; i++) {
            Client client;
            try {
                client = new Client(clientsSettings.clientSettings);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Thread thread = new Thread(client);
            thread.setDaemon(true);
            thread.start();
            clients.add(thread);
        }

        for (Thread client : clients) {
            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
