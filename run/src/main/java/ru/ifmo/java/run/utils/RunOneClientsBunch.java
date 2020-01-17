package ru.ifmo.java.run.utils;

import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.client.metrics.MetricsWriter;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;

public class RunOneClientsBunch {

    public static void runCase(RunSettings settings) throws InterruptedException {
        MetricsWriter.clean();

        ClientsSettings clientsSettings = settings.clientsSettings;
        if (settings.typeOfVariableToChange == TypeOfVariableToChange.NONE) {
            runClient(clientsSettings);
        } else {
            for (int index = settings.range.getMin();
                 index < settings.range.getMax();
                 index += settings.range.getDelta()) {
                changeClientsSettings(settings, index);
                runClient(clientsSettings);
            }
        }
    }

    private static void changeClientsSettings(RunSettings settings, int value) {
        ClientsSettings clientsSettings = settings.clientsSettings;
        switch (settings.typeOfVariableToChange) {
            case SIZE_OF_ARRAY_IN_REQUEST:
                clientsSettings.clientSettings.sizeOfArrayInRequest = value;
                break;
            case NUMBER_OF_REQUEST_BY_CLIENT:
                clientsSettings.clientSettings.numberOfRequestByClient = value;
            case SLEEP_TIME_AFTER_RESPONSE:
                clientsSettings.clientSettings.sleepTimeAfterResponse = value;
            default:
                throw new RuntimeException("change clients setting not defined");
        }
    }

    private static void runClient(ClientsSettings clientsSettings) {
        new ClientMaker(clientsSettings).run();
    }

}
