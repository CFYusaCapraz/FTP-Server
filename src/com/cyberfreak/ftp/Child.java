package com.cyberfreak.ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.LinkedList;

public class Child implements Runnable {
    private Socket incoming;
    private LinkedList<User> credentials;
    private File directory;

    public Child(Socket incoming, LinkedList<User> credetials, File directory) {
        this.incoming = incoming;
        this.credentials = credetials;
        this.directory = directory;
    }

    private void listFiles(File directory, PrintWriter out) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    if (!Files.isHidden(file.toPath())) {
                        if (file.isFile()) {
                            long size = file.length();
                            out.println(String.format("%s\t%d", file.getPath(), size));
                            out.flush();
                        } else if (file.isDirectory()) {
                            listFiles(file, out);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        InetAddress addr = incoming.getInetAddress();
        int port = incoming.getPort();
        String ipString = "";
        byte[] addressBytes = addr.getAddress();
        for (int i = 0; i < addressBytes.length; i++) {
            ipString += (addressBytes[i] & 0xFF);
            if (i < addressBytes.length - 1) {
                ipString += ".";
            }
        }
        ipString += ":" + port;

        boolean isClosed = false;
        boolean isAuthenticated = false;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(incoming.getOutputStream()));

            out.println("Welcome to my FTP Server");
            out.println("Please type `help` in order to see all avaible commands");
            out.flush();

            while (true) {
                out.print("command -> ");
                out.flush();
                String command = in.readLine();
                if (command == null)
                    break;
                else {
                    String option = command.split(" ")[0].toLowerCase();
                    switch (option) {
                        case "quit": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            System.out.println("[-] Connection closed: " + ipString);
                            out.println("Quiting from the FTP server...");
                            out.flush();
                            isClosed = true;
                        }
                            break;
                        case "login": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            String username = command.split(" ")[1];
                            String password = command.split(" ")[2];
                            boolean flag = true;
                            for (User user : credentials) {
                                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                                    isAuthenticated = true;
                                    out.println("200 Login successful.");
                                    out.flush();
                                    flag = false;
                                }
                                continue;
                            }
                            if (flag) {
                                isAuthenticated = false;
                                out.println("400 Login unsuccessful.");
                                out.flush();
                            }
                        }
                            break;
                        case "list": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            if (isAuthenticated) {
                                listFiles(directory, out);
                                out.println(".");
                            } else {
                                out.println("You have to be authenticated to use this command.");
                                out.println("Use login command to authenticate yourself.");
                                out.flush();
                            }
                        }
                            break;
                        case "get": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            if (isAuthenticated) {
                                String filename = command.split(" ")[1];
                                File f = new File(filename);
                                FileReader fr = new FileReader(f);
                                BufferedReader br = new BufferedReader(fr);
                                String l;
                                while ((l = br.readLine()) != null) {
                                    out.println(l);
                                }
                                br.close();
                                fr.close();
                                out.println(".");
                            } else {
                                out.println("You have to be authenticated to use this command.");
                                out.println("Use login command to authenticate yourself.");
                                out.flush();
                            }
                        }
                            break;
                        case "put": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            if (isAuthenticated) {
                                out.println("Please type to save your file inside the server.");
                                out.println("Termination character is `.\\n`.");
                                out.flush();

                                String filename = command.split(" ")[1];
                                File f = new File(filename);
                                FileWriter fw = new FileWriter(f);
                                PrintWriter pw = new PrintWriter(fw);

                                String l;
                                long totalBytes = 0;
                                while ((l = in.readLine()) != null) {
                                    if (l.equals(".")) {
                                        break;
                                    }
                                    totalBytes += l.length() + 1;
                                    pw.println(l);
                                }
                                pw.close();
                                fw.close();

                                out.println("201 Successfully saved the file in the server.");
                                out.println(String.format("%d Bytes transferred.", totalBytes));
                                out.flush();
                            } else {
                                out.println("You have to be authenticated to use this command.");
                                out.println("Use login command to authenticate yourself.");
                                out.flush();
                            }
                        }
                            break;
                        case "del": {
                            System.out.println(String.format("[!] Command `%s` from: %s", command, ipString));
                            if (isAuthenticated) {
                                String filename = command.split(" ")[1];
                                File f = new File(filename);
                                if (f.delete()) {
                                    out.println("200 Successfully deleted the file.");
                                } else {
                                    out.println("400 Could not delete the file.");
                                }
                            } else {
                                out.println("You have to be authenticated to use this command.");
                                out.println("Use login command to authenticate yourself.");
                                out.flush();
                            }
                        }
                            break;
                        case "help": {
                            out.println("Here are the avaible commands");
                            out.println("HELP \"See this help menu\"");
                            out.println("LOGIN <username> <password> \"Login to use all system commands\"");
                            out.println("LIST \"List all files\"");
                            out.println("GET <filename> \"Get the content of the file\"");
                            out.println("PUT <filename> \"Create a new file in the server\"");
                            out.println("DEL <filename> \"Delete the given file name\"");
                            out.println("QUIT \"Quit from the FTP Server\"");
                            out.flush();
                        }
                            break;
                        default:
                            out.println("Invalid command!\nPlease type `help` to see avaible commands");
                            out.flush();
                            break;
                    }
                    if (isClosed)
                        break;
                }
            }

            in.close();
            out.close();
            incoming.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
