package ru.ifmo.java.run;

import org.junit.Test;
import ru.ifmo.java.client.ClientsSettings;

public class RunOneCaseTest {

    @Test
    public void runDefaultIndividualThreadServerTest() throws InterruptedException {
        runDefaultIndividualThreadServer();
    }

    @Test
    public void doubleRunDefaultIndividualThreadServerTest() throws InterruptedException {
        runDefaultIndividualThreadServer();
        runDefaultIndividualThreadServer();
    }

    @Test
    public void runDefaultNotBlockingServerTest() throws InterruptedException {
        runDefaultNotBlockingServer();
    }

    @Test
    public void doubleRunDefaultNotBlockingServerTest() throws InterruptedException {
        runDefaultNotBlockingServer();
        runDefaultNotBlockingServer();
    }

    public void runDefaultIndividualThreadServer() throws InterruptedException {
        RunSettings runSettings = new RunSettings();
        runSettings.serverType = RunSettings.ServerType.INDIVIDUAL_THREAD_SERVER;
        runSettings.clientsSettings = new ClientsSettings();
        RunOneCase.runCase(runSettings);
    }

    public void runDefaultNotBlockingServer() throws InterruptedException {
        RunSettings runSettings = new RunSettings();
        runSettings.serverType = RunSettings.ServerType.NOT_BLOCKING_SERVER;
        runSettings.clientsSettings = new ClientsSettings();
        RunOneCase.runCase(runSettings);
    }
}