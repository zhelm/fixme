package org.fixme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class Market 
{
    public static void main( String[] args ) {
        try (Socket socket = new Socket("localhost", 5001)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String text;
            do {
                text = reader.readLine();

                System.out.println("Client has recieved ID of: " + text);

                writer.println("Sending ID to server: " + text);

            } while (!text.equals("-1"));


            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
//        A market has a list of instruments that can be traded.
//        When orders are received from brokers the market tries to execute it.
//          If the execution is successful, it updates the internal instrument list and sends the broker
//          an Executed message. If the order can’t be met, the market sends a Rejected message.
//The rules by which a market executes orders can be complex and you can play with
//them. This is why you build the simulator. Some simple rules that you need to respect
//is that an order can’t be executed if the instrument is not traded on the market or if the
//demanded quantity is not available (in case of Buy orders).
    }
}
