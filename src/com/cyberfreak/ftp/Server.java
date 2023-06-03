package com.cyberfreak.ftp;

import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private int PORT;

    public Server(int port) {
        this.PORT = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Connection established from: " + client.getInetAddress());
                new Thread(new Client(client)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
