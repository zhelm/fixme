package org.fixme;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class Router {
    public static ArrayList<Socket> connections = new ArrayList<Socket>();

    public static void main(String[] args) throws IOException {
        new Thread() {
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(5000);
                    while (true) {
                        Socket client1 = server.accept();
                        System.out.println("Broker Connected");
                        connections.add(client1);
                        OutputStream output = client1.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);

                        InputStream input = client1.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                        writer.println("I have assigned you to port: " + client1.getPort());
                        String confirmation = reader.readLine();
                        System.out.println("Confirmed that: " + confirmation + " has connected");
                        // send message to all brokers

                        // handle client2
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(5001);
                    while (true) {
                        Socket client1 = server.accept();
                        System.out.println("Market Connected");
                        // handle client2
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }.start();
    }

    public static void sendMessageToBroker() {

    }

    public static void sendMessageToMarket() {

    }
}
