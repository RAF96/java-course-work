package ru.ifmo.java.notBlockingServer;

import ru.ifmo.java.common.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketThread implements Runnable {
    private final Selector readSelector;
    private final Selector writeSelector;
    private final InetSocketAddress inetSocketAddress = new InetSocketAddress(Constant.serverHost, Constant.serverPort);

    public ServerSocketThread(Selector readSelector, Selector writeSelector) {
        this.readSelector = readSelector;
        this.writeSelector = writeSelector;
    }

    @Override
    public void run() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(inetSocketAddress);
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            while (!Thread.interrupted()) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    socketChannel.configureBlocking(false);
                    socketChannel.register(readSelector, SelectionKey.OP_READ);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
