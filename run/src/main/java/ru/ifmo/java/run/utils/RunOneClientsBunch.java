package ru.ifmo.java.run.utils;

import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.client.ClientsSettings;

public class RunOneClientsBunch {

    public static void runCase(RunSettings settings) throws InterruptedException {
        runClient(settings.clientsSettings);
    }

    public static void runClient(ClientsSettings clientsSettings) {
        new ClientMaker(clientsSettings).run();
    }

}
