package ru.ifmo.java.blockingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketLoop implements Runnable {
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Constant.numberThreadOfServerPool);
    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private final ServerSocket serverSocket;

    public ServerSocketLoop(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                cachedThreadPool.submit(new HandlerNewClient(socket, fixedThreadPool));
            }
        } catch (SocketException ignore) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fixedThreadPool.shutdown();
            cachedThreadPool.shutdown();
        }
    }
}
