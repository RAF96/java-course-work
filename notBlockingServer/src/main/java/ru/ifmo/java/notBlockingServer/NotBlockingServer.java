package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.notBlockingServer.utils.WriteAttachment;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class NotBlockingServer implements Runnable {

    private Thread readThread;
    private Thread writeThread;
    private Selector readSelector;
    private Selector writeSelector;
    private WorkerFactory workerFactory;
    private ConcurrentHashMap<SocketChannel, WriteAttachment> preregisterWriteChannel = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try {
            try {
                readSelector = Selector.open();
                writeSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            workerFactory = new WorkerFactory(writeSelector, preregisterWriteChannel);

            readThread = new Thread(new ReadThread(readSelector, workerFactory));
            readThread.setDaemon(true);
            readThread.start();

            writeThread = new Thread(new WriteThread(writeSelector, preregisterWriteChannel));
            writeThread.setDaemon(true);
            writeThread.start();

            new ServerSocketThread(readSelector, writeSelector).run();
        } finally {
            readThread.interrupt();
            writeThread.interrupt();
            workerFactory.close();

            try {
                readSelector.close();
            } catch (IOException ignore) {
            }

            try {
                writeSelector.close();
            } catch (IOException ignored) {
            }
        }
    }
}
