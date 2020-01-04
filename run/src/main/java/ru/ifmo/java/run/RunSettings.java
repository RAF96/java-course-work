package ru.ifmo.java.run;

import ru.ifmo.java.client.ClientsSettings;

public class RunSettings {
    public ServerType serverType;

    ;
    public ClientsSettings clientsSettings;
    public enum ServerType {NOT_BLOCKING_SERVER, BLOCKING_SERVER, INDIVIDUAL_THREAD_SERVER}

}
