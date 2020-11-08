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
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            boolean sentID = false;
            String message = "";

            do {
                
                if(!sentID) {
                    MessageHandler.sendMessage(Integer.toString(ServerSocketThread.id));
                    System.out.println("Assigning id of " + ServerSocketThread.id + " to client.");
                    sentID = true;
                }
                
                if((message = reader.readLine()) != null) {
                    System.out.println("Message= " + message);
                    MessageHandler.sendMessage(message);
                }
                
            } while(message == null || !message.equals("-1"));
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}