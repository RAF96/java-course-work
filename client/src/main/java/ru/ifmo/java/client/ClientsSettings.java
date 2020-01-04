package ru.ifmo.java.client;

public class ClientsSettings {

    public int numberOfClients = 2;
    public ClientSettings clientSettings = new ClientSettings();

    public static class ClientSettings {
        public int sizeOfArrayInRequest = 1;
        public int sleepTimeAfterResponse = 0 * 1000;
        public int numberOfRequestByClient = 1;
    }
}
