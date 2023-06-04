package com.cyberfreak.ftp;

import java.io.File;

public class App {
    public static void main(String[] args) {
        String passwordFile = args[0];
        int port = Integer.valueOf(args[1]);
        String dir = args[2];
        
        File f = new File(passwordFile);
        File d = new File(dir);

        Thread t = new Thread(new Server(port, f, d));
        t.setDaemon(true);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
