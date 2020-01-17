package ru.ifmo.java.client.utils;

public class PairOfTime {

    public long start;
    public long end;

    public long delta() {
        return end - start;
    }
}
