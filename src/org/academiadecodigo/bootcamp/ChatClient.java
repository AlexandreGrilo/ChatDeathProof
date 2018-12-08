package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ChatClient {

    private Socket server;
    private String host;
    private int port;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        ChatClient client = new ChatClient("localhost", 8080);
        client.start();
    }

    /**
     *
     * @param host
     * @param port
     */
    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     *
     */
    public void start() {

        try {
            server = new Socket(host, port);

            while (!server.isClosed()) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
                            System.out.println(serverIn.readLine());
                        } catch (IOException e) {
                            System.out.println("IO Exception: " + e.getMessage());
                        }
                    }
                });

                thread.start();

                PrintWriter serverOut = new PrintWriter(server.getOutputStream(), true);
                BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

                String message = systemIn.readLine();
                serverOut.println(message);

            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            System.exit(1);
        }
    }

}
