package org.fixme;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {
    private int port;
    public static int id;

    public ServerSocketThread(int port) {
        this.port = port;
    }

    public void run() {
            // recieve text from client
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server is listening on port " + port);

            // enables multiple connections using threads
            while (true) {
                Socket socket = serverSocket.accept();
                id++;
                Router.connections.add(new ClientModel(socket, id));

                if(socket.getLocalPort() == 5000) {
                    System.out.println("New Broker connected with id: " + id);
                } else if (socket.getLocalPort() == 5001) {
                    System.out.println("New Market connectedwith id: " + id);
                }
                // need to add this socket to a list
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
