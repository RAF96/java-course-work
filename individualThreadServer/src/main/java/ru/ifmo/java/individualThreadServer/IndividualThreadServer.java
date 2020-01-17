package ru.ifmo.java.individualThreadServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class IndividualThreadServer implements Runnable {

    public IndividualThreadServer() {
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constant.individualThreadServerPort);
            serverSocket.setSoTimeout(Constant.serverSocketTimeout);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        try {
            while (!Thread.interrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    Worker worker = new Worker(socket);
                    Thread thread = new Thread(worker);
                    thread.setDaemon(true);
                    thread.start();
                } catch (SocketTimeoutException ignore) {
                }
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
