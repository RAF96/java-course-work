package ru.ifmo.java.common;

import ru.ifmo.java.common.enums.ServerType;

import java.nio.file.Path;

public class Constant {
    public static final String serverHost = "localhost";
    public static final int numberThreadOfServerPool = 1;
    public static final int serverSocketTimeout = 1 * 1000;

    public static final Path metricsPath = Path.of("data/metrics");
    public static final Path metricsPathForOneClient = Path.of("data/metrics/forOneClient");
    public static final Path runSettingsForGUI = Path.of("data/settings_file");
    public static final Path dataPath = Path.of("data");
    public static final Path dataSavePath = Path.of("dataSave");

    public static final int individualThreadServerPort = 8081;
    public static final int blockingServerPort = 8082;
    public static final int notBlockingServerPort = 8083;

    public static int getPort(ServerType serverType) {
        switch (serverType) {
            case INDIVIDUAL_THREAD_SERVER:
                return individualThreadServerPort;
            case BLOCKING_SERVER:
                return blockingServerPort;
            case NOT_BLOCKING_SERVER:
                return notBlockingServerPort;
            default:
                throw new RuntimeException("not defined type of server");
        }
    }


}
