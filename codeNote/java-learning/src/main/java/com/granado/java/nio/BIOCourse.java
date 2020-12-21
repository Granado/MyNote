package com.granado.java.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: Yang Songlin
 * @Date: 2020/12/17 4:51 下午
 */
public class BIOCourse {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(InetSocketAddress.createUnresolved("127.0.0.1", 9191), 1024);
        while (!serverSocket.isClosed()) {

            Socket socket = serverSocket.accept();
            socket.getInputStream();
        }
    }
}
