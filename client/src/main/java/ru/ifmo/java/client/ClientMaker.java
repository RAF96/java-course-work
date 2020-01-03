package ru.ifmo.java.client;

import java.io.IOException;

public class ClientMaker {
    private final int number;

    public ClientMaker(int number) {
        this.number = number;
    }

    public void run() {
        for (int i = 0; i < number; i++) {
            Client client;
            try {
                client = new Client();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Thread thread = new Thread(client);
            thread.setDaemon(true);
            thread.start();
        }
    }
}
