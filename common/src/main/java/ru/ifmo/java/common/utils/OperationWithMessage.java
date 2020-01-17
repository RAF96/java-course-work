package ru.ifmo.java.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class OperationWithMessage {

    public static byte[] packMessage(byte[] message) {
        int serializedSize = message.length;
        return ByteBuffer.allocate(4 + message.length).putInt(serializedSize).put(message).array();
    }

    public static Message readAndUnpackMessage(InputStream inputStream) throws IOException {
        byte[] inputArray = new byte[4];
        int messageHeadSize = inputStream.read(inputArray);

        if (messageHeadSize == -1) {
            return new Message(null, true);
        }

        if (messageHeadSize != 4) {
            throw new WrongSizeOfMessageHeadException("wrong size of message head");
        }

        int bufferSize = ByteBuffer.wrap(inputArray).getInt();
        byte[] inputArrayResponse = new byte[bufferSize];
        int messageBodySize = inputStream.read(inputArrayResponse);
        if (messageBodySize != bufferSize) {
            throw new WrongSizeOfMessageBodyException("wrong size of message body");
        }

        return new Message(inputArrayResponse, false);
    }

    public static class Message {
        public final byte[] array;
        public final boolean endOfInputStream;

        public Message(byte[] array, boolean endOfInputStream) {
            this.array = array;
            this.endOfInputStream = endOfInputStream;
        }
    }

    public static class WrongSizeOfMessageHeadException extends RuntimeException {
        public WrongSizeOfMessageHeadException() {
        }

        public WrongSizeOfMessageHeadException(String message) {
            super(message);
        }
    }

    public static class WrongSizeOfMessageBodyException extends RuntimeException {
        public WrongSizeOfMessageBodyException() {
        }

        public WrongSizeOfMessageBodyException(String message) {
            super(message);
        }

    }


}
