package ru.ifmo.java.common;

import ru.ifmo.java.common.enums.ServerType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Constant {
    //public static final String serverHost = "localhost";
    public static final String serverHost = "18.216.180.203";

    public static final int numberThreadOfServerPool = 1;
    public static final int serverSocketTimeout = 1 * 1000;

    public static final Path metricsPath = Paths.get("data/metrics");
    public static final Path metricsPathForOneClient = Paths.get("data/metrics/forOneClient");
    public static final Path runSettingsForGUI = Paths.get("data/settings_file");
    public static final Path dataPath = Paths.get("data");
    public static final Path dataSavePath = Paths.get("dataSave");

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
