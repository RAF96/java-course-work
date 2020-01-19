package ru.ifmo.java.client.metrics;

import org.apache.commons.io.FileUtils;
import ru.ifmo.java.common.Constant;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class MetricsWriterForOneClient {
    public synchronized static void write(MetricType type, double value) throws IOException {
        Path path;
        path = Paths.get(Constant.metricsPathForOneClient.toString(), type.name());
        if (!path.toFile().exists()) {
            path.toFile().createNewFile();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(path, StandardOpenOption.APPEND), StandardCharsets.UTF_8))) {
            writer.write(String.valueOf(value));
            writer.write("\n");
        }
    }

    public static void clean() throws IOException {
        FileUtils.deleteDirectory(Constant.dataPath.toFile());
    }
}
