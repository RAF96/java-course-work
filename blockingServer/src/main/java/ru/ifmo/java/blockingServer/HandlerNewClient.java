package ru.ifmo.java.blockingServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandlerNewClient implements Runnable {
    private final Socket socket;
    private final ExecutorService threadPool;
    private final ExecutorService executorServiceWriteThread = Executors.newSingleThreadExecutor();
    private WorkerFactory workerFactory;
    private OutputStream output;
    private InputStream input;
    private Thread readThread;

    public HandlerNewClient(Socket socket, ExecutorService threadPool) {
        this.socket = socket;
        this.threadPool = threadPool;
    }

    private boolean createInputOutput() {
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            return false;
        }
        return true;
    }


    @Override
    public void run() {

        if (!createInputOutput()) return;

        try {
            workerFactory = new WorkerFactory(threadPool, executorServiceWriteThread, output);
            readThread = new Thread(new ReadThread(workerFactory, input));
            readThread.setDaemon(true);
            readThread.start();
            readThread.join();
        } catch (InterruptedException ignored) {
        } finally {
            threadPool.shutdown();
            executorServiceWriteThread.shutdown();
            readThread.interrupt();

            try {
                socket.close();
            } catch (IOException ignored) {
            }

        }
    }
}
