package ru.ifmo.java.notBlockingServer.utils;

import ru.ifmo.java.common.protocol.Protocol;

import java.util.List;

public class WriteAttachment {
    private final List<Integer> sortedList;
    private final Protocol.Response.Timestamps.Builder timestampBuilder;

    public WriteAttachment(List<Integer> sortedList, Protocol.Response.Timestamps.Builder timestampBuilder) {
        this.sortedList = sortedList;
        this.timestampBuilder = timestampBuilder;
    }

    public List<Integer> getSortedList() {
        return sortedList;
    }

    public Protocol.Response.Timestamps.Builder getTimestampBuilder() {
        return timestampBuilder;
    }
}
