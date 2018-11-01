package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by codecadet on 01/11/2018.
 */
public class ChatClient {

    private Socket server;
    private String name;
    private String host;
    private int port;

    public ChatClient(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public void start() {

        try {
            server = new Socket(host, port);

            while (!server.isClosed()) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
                            System.out.println("Message from server:~ " + serverIn.readLine());
                        } catch (IOException e) {
                            System.out.println("IO Exception: " + e.getMessage());
                        }
                    }
                });

                PrintWriter serverOut = new PrintWriter(server.getOutputStream(), true);
                BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

                String message = systemIn.readLine();
                serverOut.println(name + ":~ " + message);
            }


        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }

    }

}
