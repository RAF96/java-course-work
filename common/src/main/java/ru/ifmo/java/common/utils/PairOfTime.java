package ru.ifmo.java.common.utils;

public class PairOfTime {

    public long start;
    public long end;

    public long delta() {
        return end - start;
    }
}
