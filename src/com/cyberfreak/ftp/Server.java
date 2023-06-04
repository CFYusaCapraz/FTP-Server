package com.cyberfreak.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server implements Runnable {
    private int PORT;
    private File passwordFile;
    private File directory;
    private LinkedList<User> credentials = new LinkedList<User>();

    public Server(int port, File passwordFile, File dirPath) {
        this.PORT = port;
        this.passwordFile = passwordFile;
        this.directory = dirPath;

        try (FileReader fr = new FileReader(this.passwordFile); BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                String username, password;
                username = line.split(",")[0];
                password = line.split(",")[1].trim();
                User e = new User(username, password);
                credentials.add(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                InetAddress addr = client.getInetAddress();
                int port = client.getPort();
                String ipString = "";
                byte[] addressBytes = addr.getAddress();
                for (int i = 0; i < addressBytes.length; i++) {
                    ipString += (addressBytes[i] & 0xFF);
                    if (i < addressBytes.length - 1) {
                        ipString += ".";
                    }
                }
                ipString += ":" + port;

                System.out.println("[+] Connection established: " + ipString);
                new Thread(new Child(client, credentials, directory)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
