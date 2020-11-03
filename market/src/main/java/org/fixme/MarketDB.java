package org.fixme;

import java.sql.*;

public abstract class MarketDB {

    public static void executeTransaction(String instrument, int quantity, int brokerID, boolean isBuy, int marketID,
            int price) {

        // Update market table
        //

        // Update broker table
        // Insert or update broker in table
        if (checkBrokerTransaction(brokerID, getInstrumentID(instrument, marketID), marketID)) {
            updateBrokerInstrumentTransaction(getInstrumentID(instrument, marketID), brokerID, quantity, marketID, isBuy, price);
        } else {
            createBrokerInstrumentTransaction(getInstrumentID(instrument, marketID), brokerID, quantity, marketID, isBuy, price);
        }

        updateMarket(getInstrumentID(instrument, marketID), quantity, isBuy, marketID, brokerID);
        // Update market table first
    }

    public static boolean isInstrument(String Instrument, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery(
                        "Select * from Market_" + marketID + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        rs.getString("Instrument");
                    } catch (Exception e) {
                        System.out.println("No such column found 1");
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

    public static boolean checkInstrumentQuantity(String Instrument, int quantity, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int Available_Quantity = 0;
                int count = 0;
                //TODO Need to know if its buy or sell
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery(
                        "Select * from Market_" + marketID + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        Available_Quantity = rs.getInt("Available_Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found 2");
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

    public static boolean checkPriceLimit(String Instrument, int limit, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {

                //TODO Need to know if its buy or sell

                int price = 0;
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery(
                        "Select * from Market_" + marketID + " where  Instrument like '" + Instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        price = rs.getInt("Price");
                    } catch (Exception e) {
                        System.out.println("No such column found 3");
                    }
                }
                // TODO I think this is wrong
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

    public static void createBrokerInstrumentTransaction(int instrumentID, int brokerID, int quantity, int marketID,
            boolean isBuy, int price) {
        // Insert into this table
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                String newHero = "INSERT INTO MarketBroker_" + marketID
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

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
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

    public static int getInstrumentPrice(String instrument, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery(
                        "Select * from Market_" + marketID + " where Instrument like '" + instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        ret = rs.getInt("Price");
                    } catch (Exception e) {
                        System.out.println("No such column found 4");
                    }
                }
                if (count == 1) {
                    return ret;
                } else {
                    System.out.println("Something went wrong 1");
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
                        System.out.println("No such column found 5");
                    }
                }
                if (count == 1) {
                    return ret;
                } else {
                    System.out.println("Something went wrong 2");
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

    public static boolean updateMarket(int instrumentID, int quantity, Boolean isBuy, int marketID, int brokerID) {
        Connection connection = null;
        try {
            // if its a sell I need to check if broker has enough to sell
            quantity = (isBuy) ? getNewMarketQuantity(instrumentID, marketID) - quantity
                    : getNewMarketQuantity(instrumentID, marketID) + quantity;

            if (quantity < 0) {
                return false;
            }

            String sql = "UPDATE Market_" + marketID + " SET Available_Quantity = ? WHERE id = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, quantity);
                pstmt.setInt(2, instrumentID);

                pstmt.executeUpdate();
                System.out.println("Market updated");
                return true;
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
        return false;
    }

    public static boolean checkBrokerTransaction(int brokerID, int instrumentID, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery("Select * from MarketBroker_" + marketID + " where BrokerID = "
                        + brokerID + " AND InstrumentID = " + instrumentID);
                while (rs.next()) {
                    count++;
                }
                if (count == 1) {
                    return true;
                } else if (count == 0) {
                    return false;
                } else {
                    System.out.println("Something went wrong 3");
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
        return false;
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
                        ret = rs.getInt("Available_Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found 6");
                    }
                }
                if (count == 1) {
                    return ret;
                } else {
                    System.out.println("Something went wrong 4");
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

    public static int getInstrumentID(String instrument, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30); // set timeout to 30 sec.

                ResultSet rs = stmt.executeQuery(
                        "Select * from Market_" + marketID + " where Instrument like '" + instrument + "'");
                while (rs.next()) {
                    count++;
                    try {
                        ret = rs.getInt("id");
                    } catch (Exception e) {
                        System.out.println("No such column found 7");
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
}
