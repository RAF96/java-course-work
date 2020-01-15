package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;

public class BlockingServer implements Runnable {
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constant.serverPort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Thread blockingServer = new Thread(new ServerSocketLoop(serverSocket));
        blockingServer.start();
        try {
            blockingServer.join();
        } catch (InterruptedException e) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
