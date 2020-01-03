package ru.ifmo.java.common.algorithm;

import java.util.Arrays;
import java.util.List;

public class Sort {
    public static List<Integer> sort(List<Integer> numberList) {
        Integer[] array = new Integer[numberList.size()];
        numberList.toArray(array);
        int i = 0;

        while (i + 1 < array.length) {
            if (array[i] < array[i + 1]) {
                Integer c = array[i];
                array[i] = array[i + 1];
                array[i + 1] = c;
                if (i > 0) i -= 2;
            }
            i++;
        }
        return Arrays.asList(array);
    }
}
