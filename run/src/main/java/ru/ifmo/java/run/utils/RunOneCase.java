package ru.ifmo.java.run.utils;

import ru.ifmo.java.blockingServer.BlockingServer;
import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.individualThreadServer.IndividualThreadServer;
import ru.ifmo.java.notBlockingServer.NotBlockingServer;

public class RunOneCase {

    public static void runCase(RunSettings settings) throws InterruptedException {
        Thread serverThread;
        switch (settings.serverType) {
            case NOT_BLOCKING_SERVER:
                serverThread = runNotBlockingServer();
                break;
            case BLOCKING_SERVER:
                serverThread = runBlockingServer();
                break;
            case INDIVIDUAL_THREAD_SERVER:
                serverThread = runIndividualThreadServer();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + settings.serverType);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        runClient(settings.clientsSettings);

        serverThread.interrupt();
        serverThread.join();
    }

    private static Thread runBlockingServer() {
        BlockingServer blockingServer = new BlockingServer();
        Thread thread = new Thread(blockingServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    private static Thread runNotBlockingServer() {
        NotBlockingServer notBlockingServer = new NotBlockingServer();
        Thread thread = new Thread(notBlockingServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static Thread runIndividualThreadServer() {
        IndividualThreadServer individualThreadServer = new IndividualThreadServer();
        Thread thread = new Thread(individualThreadServer);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    public static void runClient(ClientsSettings clientsSettings) {
        new ClientMaker(clientsSettings).run();
    }
}