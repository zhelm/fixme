package org.fixme;

import java.sql.*;

public abstract class MarketDB {

    public static boolean isInstrument(String Instrument, int id) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt
                        .executeQuery("Select * from Market_" + id + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        rs.getString("Instrument");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if (count == 1) {
                    return true;
                } else if (count == 0) {
                    System.out.println("No Instrument found for: " + Instrument);
                    return false;
                } else {
                    System.out.println("Something very bad went wrong!!");
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("If this ever happens I will be very surpised...");
        return false;
    }

    public static boolean checkInstrumentQuantity(String Instrument, int quantity, int id) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int Available_Quantity = 0;
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt
                        .executeQuery("Select * from Market_" + id + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        Available_Quantity = rs.getInt("Available_Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if (count == 1 && quantity <= Available_Quantity) {
                    return true;
                } else if (count == 1 && quantity >= Available_Quantity) {
                    System.out.println("Out of stock!!");
                    return false;
                } else if (count == 0) {
                    System.out.println("No Instrument found for: " + Instrument);
                    return false;
                } else {
                    System.out.println("Something very bad went wrong!!");
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("If this ever happens I will be very surpised...");
        return false;
    }

    public static boolean checkPriceLimit(String Instrument, int limit, int id) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int price = 0;
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt
                        .executeQuery("Select * from Market_" + id + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        price = rs.getInt("Price");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if (count == 1 && limit <= price) {
                    return true;
                } else if (count == 1 && limit >= price) {
                    System.out.println("Price of instrument exceeds the limit. Please try again later(never).");
                    return false;
                } else if (count == 0) {
                    System.out.println("No Instrument found for: " + Instrument);
                    return false;
                } else {
                    System.out.println("Something very bad went wrong!!");
                    return false;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("If this ever happens I will be very surpised...");
        return false;
    }

    public static void executeTransaction(String instrument, int quantity, int brokerID, boolean isBuy, int marketID,
            int price) {

        // Update market table
        //

        // Update broker table
        // Insert or update broker in table
        if (checkBrokerTransaction(brokerID, getInstrumentID(instrument))) {
            updateBrokerInstrumentTransaction(getInstrumentID(instrument), brokerID, quantity, marketID, isBuy, price);
        } else {
            createBrokerInstrumentTransaction(getInstrumentID(instrument), brokerID, quantity, marketID, isBuy, price);
        }
        updateMarket(getInstrumentID(instrument), quantity, isBuy, marketID);
        // Update market table
    }

    public static void createBrokerInstrumentTransaction(int instrumentID, int brokerID, int quantity, int marketID,
            boolean isBuy, int price) {
        // Insert into this table
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Swingy.db");
                    Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                quantity = (isBuy) ? getNewQuantity(instrumentID, brokerID, marketID) + quantity
                        : getNewQuantity(instrumentID, brokerID, marketID) - quantity;

                String newHero = "INSERT INTO Marketbroker" + marketID
                        + "(BrokerID, InstrumentID, Quantity, BuyPrice) VALUES(?,?,?,?)";
                PreparedStatement pstmtVillains = conn.prepareStatement(newHero);

                pstmtVillains.setInt(1, brokerID);
                pstmtVillains.setInt(2, instrumentID);
                pstmtVillains.setInt(3, quantity);
                pstmtVillains.setInt(4, price);

                pstmtVillains.executeUpdate();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void updateBrokerInstrumentTransaction(int instrumentID, int brokerID, int quantity, int marketID,
            boolean isBuy, int price) {
        Connection connection = null;
        try {

            quantity = (isBuy) ? getNewQuantity(instrumentID, brokerID, marketID) + quantity
                    : getNewQuantity(instrumentID, brokerID, marketID) - quantity;

            String sql = "UPDATE MarketBroker_" + marketID
                    + " SET Quantity = ?, BuyPrice = ? WHERE BrokerID = ? AND InstrumentID = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Swingy.db");
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, quantity);
                pstmt.setInt(2, price);
                pstmt.setInt(3, brokerID);
                pstmt.setInt(4, instrumentID);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static int getNewQuantity(int instrumentID, int brokerID, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery("Select * from MarketBroker_" + marketID + " where InstrumentID = "
                        + instrumentID + " AND BrokerID = " + marketID);
                while (rs.next()) {
                    count++;
                    try {
                        ret = rs.getInt("Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if (count == 1) {
                    return ret;
                } else {
                    System.out.println("Something went wrong");
                    System.exit(-1);
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("If this ever happens I will be very surpised...");
        return -1;
    }

    public static void updateMarket(int instrumentID, int quantity, Boolean isBuy, int marketID) {
        Connection connection = null;
        try {

            quantity = (isBuy) ? getNewMarketQuantity(instrumentID, marketID) - quantity
                    : getNewMarketQuantity(instrumentID, marketID) + quantity;

            String sql = "UPDATE Market_" + marketID + " SET Quantity = ? WHERE id = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Swingy.db");
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, quantity);
                pstmt.setInt(2, instrumentID);

                pstmt.executeUpdate();
                System.out.println("Market updated");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

    }
    // TODO
    public static boolean checkBrokerTransaction(int brokerID, int instrumentID) {
        return true;
    }

    public static int getNewMarketQuantity(int instrumentID, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery("Select * from Market_" + marketID + " where id = " + instrumentID);
                while (rs.next()) {
                    count++;
                    try {
                        ret = rs.getInt("Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if (count == 1) {
                    return ret;
                } else {
                    System.out.println("Something went wrong");
                    System.exit(-1);
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        System.out.println("If this ever happens I will be very surpised...");
        return -1;
    }

    // TODO
    public static int getInstrumentID(String instrument) {
        return 1;
    }
}
