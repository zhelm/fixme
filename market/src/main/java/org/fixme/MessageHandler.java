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

        // id=2☺8=FIX.4.2☺35=D☺53=1☺54=1☺38=20☺44=50☺55=SOL☺50=2☺49=2☺56=1☺10=045☺
        String[] fixMessage = message.split(String.valueOf((char) 1));
        if (fixMessage.length > 6) {
            System.out.println(fixMessage[7]);
            // create message
            // Not that simple. Need to use fix protocol

            // Check market db if that specific instrument is there
            // and if there is enough of it
            // at that specific price limit

            // Well this is confusing AF
            if (MarketDB.isInstrument(fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[10].split("=")[1]))
                    && MarketDB.checkInstrumentQuantity(fixMessage[7].split("=")[1],
                            Integer.parseInt(fixMessage[10].split("=")[1]),
                            Integer.parseInt(fixMessage[10].split("=")[1]))
                    && MarketDB.checkPriceLimit(fixMessage[7].split("=")[1],
                            Integer.parseInt(fixMessage[6].split("=")[1]),
                            Integer.parseInt(fixMessage[10].split("=")[1]))) {
                // TODO get everything from fix message
                MarketDB.executeTransaction(fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[5].split("=")[1]),
                        Integer.parseInt(fixMessage[0].split("=")[1]), true,
                        Integer.parseInt(fixMessage[10].split("=")[1]), MarketDB.getInstrumentPrice(
                                fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[10].split("=")[1])));
                System.out.println("Well is did it");
                writer.println("Write the fix message here");
            } else {
                writer.println("FAILURE");
            }
        }

        // if all of that is fine then complete transaction buy(remove and add to a
        // transaction table) or sell(add and remove from transaction table)
        //
        // "id="+attach.clientId+soh+fixv+soh+"35=8"+soh+"39=8"+soh+"50="+attach.clientId+soh+"49="+attach.clientId+soh+"56="+dstId+soh;
        // 35 D means new order single
        // 35 8 means execution report
        // 35 3 means a session reject

        // Status
        // Tag Value Meaning
        // 39 0 means new order acknolodged
        // 39 8 means order is rejected
        // 39 1 means patial execution
        // 39 2 means complete execution

        // Side
        // Tag Value Meaning
        // 54 1 Buy
        // 54 2 Sell

        // OrderQty
        // Tag Value Meaning
        // 38 means the quantity of prouduct being ordered

        // Price
        // Tag Value Meaning
        // 44 Specify price limit

        // OrderType
        // Tag Value Meaning
        // 40 1 Market
        // 40 2 Limit

        // Routing
        // Tag Value Meaning
        // 50 id of sender
        // 49 id of company
        // 56 id of receiver

        // Symbol
        // Tag Value Meaning
        // 55 means value is a Symbol
    }
}
