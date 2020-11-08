package org.fixme;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {
    private int port;
    public static int id = 1000000;

    public ServerSocketThread(int port) {
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                id++;
                Router.connections.add(new ClientModel(socket, id));

                if(socket.getLocalPort() == 5000) {
                    System.out.println("New Broker connected with id: " + id);
                } else if (socket.getLocalPort() == 5001) {
                    System.out.println("New Market connectedwith id: " + id);
                }
                new ServerThread(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    public static void sendMessageToBroker() {
        
    }
  
}
