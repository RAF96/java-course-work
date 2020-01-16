package ru.ifmo.java.client.metrics;

import ru.ifmo.java.common.Constant;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MetricsWriter {
    void write(MetricType type, double value) throws IOException {
        Path path = Paths.get(Constant.metricsPath.toString(), type.name());
        DataOutputStream dataOutputStream =
                new DataOutputStream(Files.newOutputStream(path, StandardOpenOption.APPEND));
        dataOutputStream.writeDouble(value);
        dataOutputStream.writeChar('\n');
    }
}
