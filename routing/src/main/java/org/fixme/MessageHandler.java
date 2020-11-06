package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(String message) throws IOException {
        boolean sentMessage = false;
        String[] messageParts = message.split(String.valueOf((char) 1));
        // TODO If market does not exist yet
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
}
