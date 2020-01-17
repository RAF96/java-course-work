package ru.ifmo.java.client.metrics;

import ru.ifmo.java.common.Constant;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MetricsWriter {
    public synchronized static void write(MetricType type, double value, boolean forOneClient) throws IOException {
        Path path;
        if (forOneClient) {
            path = Paths.get(Constant.metricsPathForOneClient.toString(), type.name());
        } else {
            path = Paths.get(Constant.metricsPath.toString(), type.name());
        }
        if (!path.toFile().exists()) {
            path.toFile().createNewFile();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(path, StandardOpenOption.APPEND), StandardCharsets.UTF_8))) {
            writer.write(String.valueOf(value));
            writer.write("\n");
        }
    }

    public static void clean() {
        File[] files = Constant.metricsPath.toFile().listFiles();
        if (files == null) {
            throw new RuntimeException("folder isn't exist");
        }
        for (File file : files) {
            file.delete();
        }
    }
}
