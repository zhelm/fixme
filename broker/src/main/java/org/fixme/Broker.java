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

// https://www.codejava.net/java-se/networking/java-socket-server-examples-tcp-ip
public class Broker {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            int wallet = 20000;
            int id = 0;
            // OutputStream output = socket.getOutputStream();
            // PrintWriter writer = new PrintWriter(output, true);
            // buy/sell = 54
            // brokerid - 50 and 49

            String Instrument = "SOL"; // 55
            String Quantity = "20"; // 38
            String Market = "2"; // 56
            String Price = "50"; // 44
            boolean isBuy = true;

            // "id="+attach.clientId+soh+fixv+soh+"35=D"+soh+"54=1"+soh+"38=2"+soh+"44=90"+soh+"55=WTCSHIRTS"+soh;
            // msg += "50="+attach.clientId+soh+"49="+attach.clientId+soh+"56="+dst+soh;
            // • Instrument
            // • Quantity
            // • Market
            // • Price

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String text;
            String message;
            do {
                // R
                // S
                // R
                // XXXXXX
                text = reader.readLine();
                id = Integer.parseInt(text);
                // Need to see if this broker has enough money first
                message = getFixMessage(id, Instrument, Quantity, Market, Price, wallet, isBuy);
                System.out.println(message);
                MessageHandler.sendMessage(socket, message);
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

    public static String getFixMessage(int id, String Instrument, String Quantity, String Market, String Price,
            int wallet, Boolean isBuy) {
        String message = "id=" + id + (char) 1 + "8=FIX.4.2" + (char) 1 + "35=D" + (char) 1 + "53=1" + (char) 1 + "54="
                + ((isBuy) ? 1 : 2) + (char) 1 + "38=" + Quantity + (char) 1 + "44=" + Price + (char) 1 + "55="
                + Instrument + (char) 1 + "50=" + id + (char) 1 + "49=" + id + (char) 1 + "56=" + Market + (char) 1;
        int checksum = getCheckSum(message);

        return (checksum >= 100) ? (message + "10=" + checksum + (char) 1) : (message + "10=0" + checksum + (char) 1);
        // String Quantity = "20"; // 38
        // String Price = "50"; // 44
        // brokerid - 50 and 49
        // buy/sell = 54
        // String Instrument = "SOL"; // 55
        // String Market = "2"; // 56
        // boolean isBuy = true;


        // • Instrument
        // • Quantity
        // • Market
        // • Price
    }

    public static int getCheckSum(String message) {
        int checkSum = 0;
        for (int i = 0; i < message.length(); i++) {
            checkSum += (int) (message.charAt(i));
        }
        return checkSum % 256;
    }
}
