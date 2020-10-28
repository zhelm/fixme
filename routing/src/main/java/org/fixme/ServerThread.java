package org.fixme;

import java.io.*;
import java.net.*;

public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // recieve text from client
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String message;

            do {
                // S
                MessageHandler.sendMessage(Integer.toString(ServerSocketThread.id));
                // R
                message = reader.readLine();
                // S
                MessageHandler.sendMessage(message);
                // R
                message = reader.readLine();
                // S
                MessageHandler.sendMessage(message);
            } while(!message.equals("-1"));
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}