package ru.ifmo.java.run.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.ifmo.java.client.ClientMaker;
import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.client.metrics.MetricsWriterForOneClient;
import ru.ifmo.java.common.Constant;
import ru.ifmo.java.common.enums.MetricType;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Logger;

public class RunOneClientsBunch {

    public static void runCase(RunSettings settings) throws InterruptedException {
        Logger logger = Logger.getLogger("global");

        logger.info("Start run case");

        filesOperation(settings);

        ClientsSettings clientsSettings = settings.clientsSettings;
        if (settings.typeOfVariableToChange == TypeOfVariableToChange.NONE) {
            runClient(clientsSettings);
            try {
                collectStats(0); // MOCK
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (int index = settings.range.getMin();
                 index < settings.range.getMax();
                 index += settings.range.getDelta()) {
                changeClientsSettings(settings, index);
                runClient(clientsSettings);

                try {
                    collectStats(index);
                } catch (IOException e) {
                    e.printStackTrace();
                    return; // MOCK
                }
            }
        }

        logger.info("Finish run case");
    }

    private static void collectStats(int x) throws IOException {
        for (MetricType type : MetricType.values()) {
            Path in = Paths.get(Constant.metricsPathForOneClient.toString(), type.name());
            List<String> values = Files.readAllLines(in);
            OptionalDouble optinalY = values.stream().mapToDouble(Double::parseDouble).average();
            if (!optinalY.isPresent()) {
                throw new RuntimeException("file doesn't have data");
            }
            double y = optinalY.getAsDouble();

            Path out = Paths.get(Constant.metricsPath.toString(), type.name());
            if (!out.toFile().exists()) {
                out.toFile().createNewFile();
            }
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    Files.newOutputStream(out, StandardOpenOption.APPEND), StandardCharsets.UTF_8))) {
                writer.write(String.valueOf(x));
                writer.write(" ");
                writer.write(String.valueOf(y));
                writer.write("\n");
            }
        }
    }


    private static void filesOperation(RunSettings settings) {
        try {
            MetricsWriterForOneClient.clean();
        } catch (IOException e) {
            throw new RuntimeException("problems with cleaning metrics files");
        }

        if (!Constant.metricsPath.toFile().exists()) {
            Constant.metricsPath.toFile().mkdirs();
        }

        if (!Constant.metricsPathForOneClient.toFile().exists()) {
            Constant.metricsPathForOneClient.toFile().mkdirs();
        }

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
            case NUMBER_OF_CLIENTS:
                clientsSettings.numberOfClients = value;
                break;
            case SLEEP_TIME_AFTER_RESPONSE:
                clientsSettings.clientSettings.sleepTimeAfterResponse = value;
                break;
            default:
                throw new RuntimeException("change clients setting not defined");
        }
    }

    private static void runClient(ClientsSettings clientsSettings) {
        new ClientMaker(clientsSettings).run();
    }

}
