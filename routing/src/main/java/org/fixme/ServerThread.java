package org.fixme;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible to handle client connection.
 *
 * @author www.codejava.net
 */
public class ServerThread extends Thread {
    private Socket socket;
    private static int id = 1;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // recieve text from client
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Send text to client
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            String text;

            System.out.println("Sending id to client: " + id);
            writer.println(id);
            do {
                // recieve messages with incomming id and dest id. This then tells me where to send messages to. But how to send messages to specific port and id????
                text = reader.readLine();
                System.out.println("Ack: " + text);
                id++;
            } while(!text.equals("-1"));
            socket.close();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}