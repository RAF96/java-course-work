package ru.ifmo.java.run;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.enums.ServerType;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;
import ru.ifmo.java.run.utils.RunOneClientsBunch;
import ru.ifmo.java.run.utils.RunSettings;

public class RunOneClientsBunchTest {

    private static RunServers runServers;

    @BeforeClass
    public static void runServers() {
        runServers = RunServers.getInstance();
    }

    @AfterClass
    public static void interruptServers() throws Exception {
        runServers.close();
    }

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

    @Test
    public void runDefaultBlockingServerTest() throws InterruptedException {
        runDefaultBlockingServer();
    }

    @Test
    public void doubleRunDefaultBlockingServerTest() throws InterruptedException {
        runDefaultBlockingServer();
        runDefaultBlockingServer();
    }


    public void runDefaultIndividualThreadServer() throws InterruptedException {
        RunSettings runSettings = new RunSettings();
        runSettings.serverType = ServerType.INDIVIDUAL_THREAD_SERVER;
        runSettings.clientsSettings = new ClientsSettings();
        runSettings.clientsSettings.serverPort = Constant.getPort(runSettings.serverType);
        runSettings.typeOfVariableToChange = TypeOfVariableToChange.NONE;
        RunOneClientsBunch.runCase(runSettings);
    }

    public void runDefaultNotBlockingServer() throws InterruptedException {
        RunSettings runSettings = new RunSettings();
        runSettings.serverType = ServerType.NOT_BLOCKING_SERVER;
        runSettings.clientsSettings = new ClientsSettings();
        runSettings.clientsSettings.serverPort = Constant.getPort(runSettings.serverType);
        runSettings.typeOfVariableToChange = TypeOfVariableToChange.NONE;
        RunOneClientsBunch.runCase(runSettings);
    }

    public void runDefaultBlockingServer() throws InterruptedException {
        RunSettings runSettings = new RunSettings();
        runSettings.serverType = ServerType.BLOCKING_SERVER;
        runSettings.clientsSettings = new ClientsSettings();
        runSettings.clientsSettings.serverPort = Constant.getPort(runSettings.serverType);
        runSettings.typeOfVariableToChange = TypeOfVariableToChange.NONE;
        RunOneClientsBunch.runCase(runSettings);
    }
}