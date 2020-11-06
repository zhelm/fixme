package org.fixme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Market {
    public static int id = 0;
    public static void main(String[] args) throws SQLException {
        try (Socket socket = new Socket("localhost", 5001)) {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String text;
            do {
                text = reader.readLine();
                if (id == 0) {
                    id = Integer.parseInt(text);
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                            Statement stmt = conn.createStatement()) {
                        String MarketTable = "CREATE TABLE IF NOT EXISTS Market_" + id + " (\n"
                                + "	id integer PRIMARY KEY,\n" + "	Instrument text NOT NULL Unique,\n"
                                + "	Available_Quantity integer NOT NULL,\n" + "	Price integer NOT NULL\n" + ");";

                        String MarketBroker = "CREATE TABLE IF NOT EXISTS MarketBroker_" + text + " (\n"
                                + "	id integer PRIMARY KEY,\n" + "	BrokerID integer NOT NULL,\n"
                                + "	InstrumentID integer NOT NULL,\n" + "Quantity integer NOT NULL,\n" + " BuyPrice integer NOT NULL );";

                        String MarketData = "INSERT or Ignore INTO Market_" + id +"(Instrument, Available_Quantity, Price) VALUES(?,?,?)";

                        stmt.setQueryTimeout(30);
                        stmt.execute(MarketTable);
                        stmt.execute(MarketBroker);

                        PreparedStatement pstmtMarketData = conn.prepareStatement(MarketData);

                        pstmtMarketData.setString(1, "GOLD");
                        pstmtMarketData.setInt(2, 100);
                        pstmtMarketData.setInt(3, 20);
                        pstmtMarketData.executeUpdate();

                        pstmtMarketData.setString(1, "OIL");
                        pstmtMarketData.setInt(2, 300);
                        pstmtMarketData.setInt(3, 10);
                        pstmtMarketData.executeUpdate();

                        pstmtMarketData.setString(1, "WOOD");
                        pstmtMarketData.setInt(2, 20000);
                        pstmtMarketData.setInt(3, 1);
                        pstmtMarketData.executeUpdate();

                        pstmtMarketData.setString(1, "COAL");
                        pstmtMarketData.setInt(2, 10);
                        pstmtMarketData.setInt(3, 500);
                        pstmtMarketData.executeUpdate();

                        pstmtMarketData.setString(1, "IRON");
                        pstmtMarketData.setInt(2, 1000);
                        pstmtMarketData.setInt(3, 30);
                        pstmtMarketData.executeUpdate();

                    }
                }
                MessageHandler.sendMessage(socket, text);

            } while (!text.equals("-1"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
