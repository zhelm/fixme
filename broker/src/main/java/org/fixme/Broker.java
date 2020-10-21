package org.fixme;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
// https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
public class Broker {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {

            // OutputStream output = socket.getOutputStream();
            // PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String text;
            do {
                MessageHandler.sendMessage(socket);
                text = reader.readLine();
                System.out.println(text);

            } while (!text.equals("-1"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }

        // • Instrument
        // • Quantity
        // • Market
        // • Price
        // The Broker will send two types of messages:
        // • Buy. - An order where the broker wants to buy an instrument
        // • Sell. - An order where the broker want to sell an instrument
        // and will receive from the market messages of the following types:
        // • Executed - when the order was accepted by the market and the action
        // succeeded
        // • Rejected - when the order could not be met
    }
}
