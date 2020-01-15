package ru.ifmo.java.client;

public class ClientsSettings {

    public final int numberOfClients = 2;
    public final ClientSettings clientSettings = new ClientSettings();

    public static class ClientSettings {
        public final int sizeOfArrayInRequest = 1;
        public final int sleepTimeAfterResponse = 0 * 1000;
        public final int numberOfRequestByClient = 1;
    }
}
