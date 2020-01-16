package ru.ifmo.java.run.utils;

import ru.ifmo.java.client.ClientsSettings;

public class RunSettings {
    public ServerType serverType;
    public ClientsSettings clientsSettings;
    public ChangeVariableType changeVariableType;

    public RunSettings() {
    }

    public RunSettings(ServerType serverType, ClientsSettings clientsSettings) {
        this.serverType = serverType;
        this.clientsSettings = clientsSettings;
    }

    public enum ServerType {NOT_BLOCKING_SERVER, BLOCKING_SERVER, INDIVIDUAL_THREAD_SERVER}

    public enum ChangeVariableType {
        NONE, SIZE_OF_ARRAY_IN_REQUEST,
        SLEEP_TIME_AFTER_RESPONSE, NUMBER_OF_REQUEST_BY_CLIENT
    }

}
