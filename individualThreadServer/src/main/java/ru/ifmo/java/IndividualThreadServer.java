package ru.ifmo.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IndividualThreadServer implements Runnable {

    public IndividualThreadServer() {
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
             serverSocket = new ServerSocket(Constant.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        try {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                Worker worker = new Worker(socket);
                Thread thread = new Thread(worker);
                thread.setDaemon(true);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
