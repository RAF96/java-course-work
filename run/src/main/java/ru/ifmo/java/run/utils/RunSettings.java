package ru.ifmo.java.run.utils;

import ru.ifmo.java.client.ClientsSettings;
import ru.ifmo.java.common.enums.ServerType;
import ru.ifmo.java.common.enums.TypeOfVariableToChange;

public class RunSettings {
    public ServerType serverType;
    public ClientsSettings clientsSettings = new ClientsSettings();
    public TypeOfVariableToChange typeOfVariableToChange;
    public Range range;

    public RunSettings() {
    }

    public static class Range {
        private int min;
        private int max;
        private int delta;

        public Range(int min, int max, int delta) {
            if (min >= max) {
                throw new RuntimeException();
            }
            this.min = min;
            this.max = max;
            this.delta = delta;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public int getDelta() {
            return delta;
        }
    }

}
