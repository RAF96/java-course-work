package ru.ifmo.java.run;

import ru.ifmo.java.blockingServer.BlockingServer;
import ru.ifmo.java.individualThreadServer.IndividualThreadServer;
import ru.ifmo.java.notBlockingServer.NotBlockingServer;

public class RunServers implements AutoCloseable {

    private static RunServers instance;
    private Thread notBlockingServer;
    private Thread blockingServer;
    private Thread individualThreadServer;

    private RunServers() {
        runServers();
    }

    public static void main(String[] args) {
        RunServers instance = getInstance();
        try {
            instance.individualThreadServer.join();
            instance.blockingServer.join();
            instance.notBlockingServer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static synchronized RunServers getInstance() {
        if (instance == null) {
            instance = new RunServers();
        }
        return instance;
    }

    public void runServers() {
        individualThreadServer = runIndividualThreadServer();
        blockingServer = runBlockingServer();
        notBlockingServer = runNotBlockingServer();
    }

    public void shutdownServers() {
        individualThreadServer.interrupt();
        blockingServer.interrupt();
        notBlockingServer.interrupt();
    }

    @Override
    public void close() throws Exception {
        shutdownServers();
    }


    private Thread runBlockingServer() {
        BlockingServer blockingServer = new BlockingServer();
        Thread thread = new Thread(blockingServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private Thread runNotBlockingServer() {
        NotBlockingServer notBlockingServer = new NotBlockingServer();
        Thread thread = new Thread(notBlockingServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private Thread runIndividualThreadServer() {
        IndividualThreadServer individualThreadServer = new IndividualThreadServer();
        Thread thread = new Thread(individualThreadServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}
