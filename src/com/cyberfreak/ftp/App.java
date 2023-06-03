package com.cyberfreak.ftp;

public class App {
    public static void main(String[] args){
        Thread t = new Thread(new Server(8008));
        t.setDaemon(true);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
