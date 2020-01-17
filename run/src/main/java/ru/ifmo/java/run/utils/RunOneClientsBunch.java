package ru.ifmo.java.run.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.client.metrics.MetricsWriter;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class RunOneClientsBunch {

    public static void runCase(RunSettings settings) throws InterruptedException {
        filesOperation(settings);

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

    private static void filesOperation(RunSettings settings) {
        if (!Constant.metricsPath.toFile().exists()) {
            Constant.metricsPath.toFile().mkdirs();
        }
        MetricsWriter.clean();

        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(settings);

            if (!Constant.runSettingsForGUI.toFile().exists()) {
                Constant.runSettingsForGUI.toFile().createNewFile();
            }

            DataOutputStream dataOutputStream =
                    new DataOutputStream(Files.newOutputStream(Constant.runSettingsForGUI,
                            StandardOpenOption.APPEND));
            dataOutputStream.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
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
