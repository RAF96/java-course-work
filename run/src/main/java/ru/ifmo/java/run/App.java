package ru.ifmo.java.run;

import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.common.DefaultSetting;
import ru.ifmo.java.individualThreadServer.IndividualThreadServer;
import ru.ifmo.java.notBlockingServer.NotBlockingServer;

public class App {

    public static void main(String... args) throws InterruptedException {
        runIndividualThreadServer();
        // runNotBlockingServer();
        Thread.sleep(1 * 1000);
        runClient(DefaultSetting.numberOfClients);
        System.out.println("App's finish");
        Thread.sleep(1000000); // MOCK
    }

    private static void runNotBlockingServer() {
        NotBlockingServer notBlockingServer = new NotBlockingServer();
        Thread thread = new Thread(notBlockingServer);
        thread.setDaemon(true);
        thread.start();
    }

    public static void runIndividualThreadServer() {
        IndividualThreadServer individualThreadServer = new IndividualThreadServer();
        Thread thread = new Thread(individualThreadServer);
        thread.setDaemon(true);
        thread.start();
    }

    public static void runClient(int number) {
        new ClientMaker(number).run();
    }
}
