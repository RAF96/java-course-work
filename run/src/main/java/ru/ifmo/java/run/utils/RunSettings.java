package ru.ifmo.java.run.utils;

import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.common.enums.ChangeVariableType;
import ru.ifmo.java.common.enums.ServerType;

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


}
