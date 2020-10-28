package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(String message) throws IOException {
        String[] messageParts = message.split(String.valueOf((char) 1));
        if (messageParts.length == 1) {
            for (ClientModel client : Router.connections) {
                if (client.id == Integer.parseInt(message)) {
                    OutputStream output = client.socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    writer.println(message);
                }
            }
        } else if (messageParts.length > 1) {
            System.out.println("Message length: " + messageParts.length);
            int destinationId = Integer.parseInt(messageParts[10].split("=")[1]);
            for (ClientModel client : Router.connections) {
                if(client.id == destinationId) {
                    OutputStream output = client.socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    writer.println(message);
                }
            }
            System.out.println("Destinationd id " + destinationId);
        }
    }
}
