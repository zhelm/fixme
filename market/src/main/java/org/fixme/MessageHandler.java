package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(Socket socket, String message) throws IOException {
        // Try and complete the fix request
        System.out.println("Message recieved from broker:\n\t " + message);
        // reply with Executed or Rejected

        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        // create message 
        writer.println("Executed");
        // Not that simple. Need to use fix protocol
        // "id="+attach.clientId+soh+fixv+soh+"35=8"+soh+"39=8"+soh+"50="+attach.clientId+soh+"49="+attach.clientId+soh+"56="+dstId+soh;
// 35       D      means new order single
// 35       8      means execution report
// 35       3      means a session reject

// Status
// Tag     Value   Meaning
// 39        0     means new order acknolodged
// 39        8     means order is rejected
// 39        1     means patial execution
// 39        2     means complete execution

// Side
// Tag     Value   Meaning
// 54        1     Buy
// 54        2     Sell

// OrderQty
// Tag     Value   Meaning
// 38              means the quantity of prouduct being ordered

// Price
// Tag     Value   Meaning
// 44              Specify price limit

// OrderType
// Tag     Value   Meaning
// 40       1      Market
// 40       2      Limit

// Routing
// Tag     Value   Meaning
// 50              id of sender
// 49              id of company
// 56              id of receiver

// Symbol
// Tag     Value   Meaning
// 55                  means value is a Symbol
    }
}
