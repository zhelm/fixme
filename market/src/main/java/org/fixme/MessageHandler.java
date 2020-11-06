package org.fixme;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class MessageHandler {
    public MessageHandler() {
    }

    public static void sendMessage(Socket socket, String message) throws IOException {
        System.out.println("Message recieved from broker:\n\t " + message);
        // TODO Checksum

        OutputStream output = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        String[] fixMessage = message.split(String.valueOf((char) 1));
        if (fixMessage.length > 6) {
            if (MarketDB.isInstrument(fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[10].split("=")[1]))
                    && MarketDB.checkInstrumentQuantity(fixMessage[7].split("=")[1],
                            Integer.parseInt(fixMessage[5].split("=")[1]),
                            Integer.parseInt(fixMessage[10].split("=")[1]),
                            Integer.parseInt(fixMessage[0].split("=")[1]),
                            (Integer.parseInt(fixMessage[4].split("=")[1]) == 1) ? true : false)
                    && MarketDB.checkPriceLimit(fixMessage[7].split("=")[1],
                            Integer.parseInt(fixMessage[6].split("=")[1]),
                            Integer.parseInt(fixMessage[10].split("=")[1]),
                            (Integer.parseInt(fixMessage[4].split("=")[1]) == 1) ? true : false)) {
                MarketDB.executeTransaction(fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[5].split("=")[1]),
                        Integer.parseInt(fixMessage[0].split("=")[1]),
                        (Integer.parseInt(fixMessage[4].split("=")[1]) == 1) ? true : false,
                        Integer.parseInt(fixMessage[10].split("=")[1]), MarketDB.getInstrumentPrice(
                                fixMessage[7].split("=")[1], Integer.parseInt(fixMessage[10].split("=")[1])));
                System.out.println("Transaction has been completed");
                writer.println(getFixMessage(fixMessage, true));
            } else {
                writer.println(getFixMessage(fixMessage, false));
            }
        }
    }

    public static String getFixMessage(String[] message, boolean succeeded) {
        String mes = "id=" + message[10].split("=")[1] + (char) 1 + "8=FIX.4.2" + (char) 1 + "35=8" + (char) 1 + "39="
                + ((succeeded) ? 2 : 8) + (char) 1 + "50=" + message[10].split("=")[1] + (char) 1 + "49="
                + message[10].split("=")[1] + (char) 1 + "56=" + message[0].split("=")[1] + (char) 1;
        int checksum = getCheckSum(mes);
        return (checksum >= 100) ? (mes + "10=" + checksum + (char) 1) : (mes + "10=0" + checksum + (char) 1);
    }

    public static int getCheckSum(String message) {
        int checkSum = 0;
        for (int i = 0; i < message.length(); i++) {
            checkSum += (int) (message.charAt(i));
        }
        return checkSum % 256;
    }
}
