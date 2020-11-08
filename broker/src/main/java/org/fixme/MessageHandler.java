package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(Socket socket, String message) throws IOException {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(message);
    }
}
