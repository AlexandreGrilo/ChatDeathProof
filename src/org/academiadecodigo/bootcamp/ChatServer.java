package org.academiadecodigo.bootcamp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatServer {

    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private ServerSocket serverSocket = null;
    private ArrayList<ClientHandler> clientHandlers;
    private int port;


    /**
     * @param args
     */
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(8080);
        chatServer.start();
    }

    /**
     * CONSTRUCTOR INITIALIZE CLIENT HANDLER ARRAY LIST AND SERVER SOCKET
     *
     * @param port
     */
    public ChatServer(int port) {
        this.port = port;
        clientHandlers = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO, Message.SERVER_BIND  + getAddres(serverSocket));
        } catch (IOException e) {
            System.out.println(Message.IO_EXCEPTION + e.getMessage());
        }
    }

    // START INITIALIZE EXECUTOR SERVICE AND ACCEPT CLIENT HANDLER
    private void start() {

        try {

            ExecutorService executorService = Executors.newFixedThreadPool(50);

            while (!serverSocket.isClosed()) {

                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                executorService.submit(clientHandler);
                clientHandlers.add(clientHandler);

            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, Message.SERVER_NOT_BIND + port);
            logger.log(Level.SEVERE, e.getMessage());
            System.exit(1);
        }

    }

    /**
     * SEND MESSAGES TO EVERYBODY CONNECTED
     *
     * @param name
     * @param message
     */
    public void sendAll(String name, String message) {
        synchronized (clientHandlers) {

            for (ClientHandler client : clientHandlers) {
                client.send(name + ":~ " + message);
            }
        }
    }

    /**
     * GETTER SERVER SOCKET ADDRESS
     *
     * @param serverSocket
     * @return
     */
    private String getAddres(ServerSocket serverSocket) {

        if (serverSocket == null) {
            return null;
        }

        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort();
    }


    // CLIENT HANDLER INNER CLASS
    public class ClientHandler implements Runnable {

        private BufferedReader serverIn;
        private PrintWriter systemOut;
        private Socket client;
        private String name = "";

        /**
         * @param accept
         */
        public ClientHandler(Socket accept) {
            this.client = accept;
        }

        /**
         * @param message
         */
        public void send(String message) {

            if (message != null) {
                systemOut.println(message);
            }
        }

        @Override
        public void run() {

            try {

                serverIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
                systemOut = new PrintWriter(client.getOutputStream(), true);

                systemOut.println(Message.INSERT_NAME);
                name = serverIn.readLine();

                if (name.equals("")) {
                    systemOut.println(Message.NAME_NULL);
                    client.close();
                    return;
                }
                systemOut.println(Message.WELCOME);
                sendAll(name, Message.JOINED);
                System.out.println(name + Message.CONNECTED);

                while (true) {
                    String message = serverIn.readLine();

                    if (message.equals(Message.EXIT)) {
                        systemOut.println(Message.THANKS);
                        sendAll(name, Message.LEAVED_THE_CHAT);
                        client.close();
                        clientHandlers.remove(this);
                        continue;
                    }

                    sendAll(name, message);
                }

            } catch (IOException e) {
                System.out.println(Message.IO_EXCEPTION + e.getMessage());

            } finally {
                try {
                    client.close();
                    clientHandlers.remove(this);
                } catch (IOException e) {
                    System.out.println(Message.IO_EXCEPTION + e.getMessage());
                }
                System.out.println(name + Message.DISCONNECTED);
            }
        }

    } // CLOSE INNER CLASS

}
