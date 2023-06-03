package com.cyberfreak.ftp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    protected Socket incoming;

    public Client(Socket incoming) {
        this.incoming = incoming;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));

            out.println("Welcome to my FTP Server");
            out.println("Please type `help` in order to see all avaible commands");
            out.flush();

            while (true) {
                String command = in.readLine();

                if (command == null)
                    break;
            }

            in.close();
            out.close();
            incoming.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
