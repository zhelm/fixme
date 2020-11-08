package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(String message) throws IOException {
        boolean sentMessage = false;
        if(checkCheckSum(message)) {
            String[] messageParts = message.split(String.valueOf((char) 1));
            if (messageParts.length == 1) {
                for (ClientModel client : Router.connections) {
                    if (client.id == Integer.parseInt(message)) {
                        OutputStream output = client.socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        writer.println(message);
                        sentMessage = true;
                        break;
                    }
                }
            } else if (messageParts.length == 12) {
                System.out.println("Message length: " + messageParts.length);
                int destinationId = Integer.parseInt(messageParts[10].split("=")[1]);
                for (ClientModel client : Router.connections) {
                    if(client.id == destinationId) {
                        OutputStream output = client.socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        writer.println(message);
                        sentMessage = true;
                        break;
                    }
                }
                
                System.out.println("Destinationd id " + destinationId);
            } else if (messageParts.length == 8) {
                int destinationId = Integer.parseInt(messageParts[6].split("=")[1]);
                for (ClientModel client : Router.connections) {
                    if(client.id == destinationId) {
                        OutputStream output = client.socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(output, true);
                        writer.println(message);
                        sentMessage = true;
                        break;
                    }
                }
            }
            System.out.println("I am out of the loop");
            if(!sentMessage) {
                returnErrorMessage(message);
            }
        } else {
            returnErrorMessage(message);
        }
    }

    public static void returnErrorMessage(String message) throws IOException {
        System.out.println(message);
        String[] messageParts = message.split(String.valueOf((char) 1));
        System.out.println("Message length: " + messageParts.length);
        System.out.println("Hello : " + messageParts[0]);
            int destinationId = Integer.parseInt(messageParts[0].split("=")[1]);
            for (ClientModel client : Router.connections) {
                if(client.id == destinationId) {
                    OutputStream output = client.socket.getOutputStream();
                    System.out.println("Sending error message");
                    PrintWriter writer = new PrintWriter(output, true);
                    writer.println("-1");
                    break;
                }
            }
    }

    public static int getCheckSum(String message) {
        int checkSum = 0;
        for (int i = 0; i < message.length(); i++) {
            checkSum += (int) (message.charAt(i));
        }
        return checkSum % 256;
    }

    public static boolean checkCheckSum(String message) {
        if(message.length() < 4) {
            return true;
        }
        // id=2☺8=FIX.4.2☺35=8☺39=2☺50=2☺49=2☺56=3☺
        // 10=169☺
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
