package org.fixme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class Broker {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            if(args.length != 5) {
                System.out.println("Not enough arguments.");
                return;
            }
            int wallet = 20000;
            int id = 0;

            String Instrument = args[0];
            String Quantity = args[1]; 
            String Market = args[2]; 
            String Price = args[3];
            boolean isBuy = (args[4].equals("1") ? true : false);
            try {
                Integer.parseInt(args[1]);
                Integer.parseInt(args[2]);
                Integer.parseInt(args[3]);
                Integer.parseInt(args[4]);
            } catch (Exception e) {
                System.out.println("One of the values that need to be numeric, is not please try again.");
            } 


            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String text;
            String message;
            do {
                text = reader.readLine();
                System.out.println("Message = " + text);
                if(id == 0) {
                    id = Integer.parseInt(text);
                    message = getFixMessage(id, Instrument, Quantity, Market, Price, wallet, isBuy);
                    MessageHandler.sendMessage(socket, message);
                }
                String[] marketMessage = text.split(String.valueOf((char)1));
                System.out.println(marketMessage.length);
                if(marketMessage.length > 2) {
                    if(marketMessage[2].split("=")[1].equals("8")) {
                        System.out.println("Report recieved from Market with id " + marketMessage[0].split("=")[1]);
                        System.out.println("Fix message: " + text.replaceAll(String.valueOf((char)1),"|"));
                        System.out.println("The checksum has " + ((checkCheckSum(text)) ? "suceeded": "failed"));
                        System.out.println("Transaction " + ((marketMessage[3].split("=")[1].equals("2"))? "Succeeded.":"Failed."));
                        break;
                    }
                }

            } while (!text.equals("-1"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }

    }

    public static String getFixMessage(int id, String Instrument, String Quantity, String Market, String Price,
            int wallet, Boolean isBuy) {
        String message = "id=" + id + (char) 1 + "8=FIX.4.2" + (char) 1 + "35=D" + (char) 1 + (char) 1 + "54="
                + ((isBuy) ? 1 : 2) + (char) 1 + "38=" + Quantity + (char) 1 + "44=" + Price + (char) 1 + "55="
                + Instrument + (char) 1 + "50=" + id + (char) 1 + "49=" + id + (char) 1 + "56=" + Market + (char) 1;
        int checksum = getCheckSum(message);

        return (checksum >= 100) ? (message + "10=" + checksum + (char) 1) : (message + "10=0" + checksum + (char) 1);
    }

    public static int getCheckSum(String message) {
        int checkSum = 0;
        for (int i = 0; i < message.length(); i++) {
            checkSum += (int) (message.charAt(i));
        }
        return checkSum % 256;
    }

    public static boolean checkCheckSum(String message) {
        String messageChecksum = message.substring(message.length() - 7, message.length());
        String fixMessage = message.substring(0, message.length() - 7);
        int initialChecksum = getCheckSum(fixMessage);

        messageChecksum = messageChecksum.split("=")[1];
        messageChecksum = messageChecksum.substring(0, messageChecksum.length() - 1);

        int validateChecksum = Integer.parseInt(messageChecksum);
        System.out.println("Validate Checksum = " + validateChecksum);

        if(initialChecksum == validateChecksum) {
            System.out.println("Checksum has matched");
            return true;
        } else {
            return false;
        }
    }
}
