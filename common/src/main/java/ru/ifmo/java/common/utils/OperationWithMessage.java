package ru.ifmo.java.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class OperationWithMessage {

    public static byte[] packMessage(byte[] message) {
        int serializedSize = message.length;
        return ByteBuffer.allocate(4 + message.length).putInt(serializedSize).put(message).array();
    }

    public static byte[] readAndUnpackMessage(InputStream inputStream) throws IOException {
        byte[] inputArray = new byte[4];
        inputStream.read(inputArray);
        int size = ByteBuffer.wrap(inputArray).getInt();
        byte[] inputArrayResponse = new byte[size];
        inputStream.read(inputArrayResponse);
        return inputArrayResponse;
    }
}
