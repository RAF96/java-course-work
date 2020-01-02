package ru.ifmo.java;

public class App {

    public static void main(String ... args) throws InterruptedException {
        runIndividualThreadServer();
        runClient(DefaultSetting.numberOfClients);
        Thread.sleep(10000);
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
