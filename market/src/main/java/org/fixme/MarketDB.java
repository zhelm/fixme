package org.fixme;

import java.sql.*;

public abstract class MarketDB {

    public static boolean executeTransaction(String instrument, int quantity, int brokerID, boolean isBuy, int marketID,
            int price) {

        try {
            if (checkBrokerTransaction(brokerID, getInstrumentID(instrument, marketID), marketID, isBuy, quantity)) {
                updateBrokerInstrumentTransaction(getInstrumentID(instrument, marketID), brokerID, quantity, marketID, isBuy, price);
            } else {
                createBrokerInstrumentTransaction(getInstrumentID(instrument, marketID), brokerID, quantity, marketID, isBuy, price);
            }
    
            updateMarket(getInstrumentID(instrument, marketID), quantity, isBuy, marketID, brokerID);
            return true;
        } catch (Exception e) {
            System.out.println("Something went wrong with execution");
            return false;
        }
    }

    public static boolean isInstrument(String Instrument, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                stmt.setQueryTimeout(30);

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
                    System.out.println("No Instrument found for 1: " + Instrument);
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
        System.out.println("If this ever happens I will be very surpised... 1");
        return false;
    }

    public static boolean checkInstrumentQuantity(String Instrument, int quantity, int marketID, int brokerID, boolean isBuy) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int Available_Quantity = 0;
                int count = 0;
                boolean isBroker = false;

                stmt.setQueryTimeout(30);
                ResultSet rs;
                if (isBuy) {
                    rs = stmt.executeQuery("Select * from Market_" + marketID + " where  Instrument like '" + Instrument + "'");
                } else {
                    isBroker = true;
                    rs = stmt.executeQuery("Select * from MarketBroker_" + marketID + " where  BrokerID = " + brokerID);
                }
                while (rs.next()) {
                    count++;
                    try {
                        if (isBuy) {
                            Available_Quantity = rs.getInt("Available_Quantity");
                        } else {
                            Available_Quantity = rs.getInt("Quantity");
                        }
                    } catch (Exception e) {
                        System.out.println("No such column found 2");
                    }
                }
                if (count == 1 && quantity <= Available_Quantity) {
                    return true;
                } else if (count == 1 && quantity >= Available_Quantity) {
                    System.out.println("Out of stock or not enough stock!!");
                    return false;
                } else if (count == 0 && isBroker) {
                    System.out.println("No previous instrument data found for broker.");
                    return false;
                } else if (count == 0){
                    System.out.println("No Instrument found for 2: " + Instrument);
                    return false;
                } else {
                    System.out.println("Something very bad went wrong!!");
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
        System.out.println("If this ever happens I will be very surpised... 2");
        return false;
    }

    public static boolean checkPriceLimit(String Instrument, int limit, int marketID, boolean isBuy) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {


                int price = 0;
                int count = 0;
                stmt.setQueryTimeout(30);

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

                if ((count == 1 && limit >= price && isBuy) || (count == 1 && limit <= price && !isBuy)) {
                    return true;
                } else if ((count == 1 && limit <= price && isBuy) || (count == 1 && limit >= price && !isBuy)) {
                    System.out.println("Price of instrument exceeds the limit. Please try again later(never).");
                    return false;
                } else if (count == 0) {
                    System.out.println("No Instrument found for 3: " + Instrument);
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
        System.out.println("If this ever happens I will be very surpised... 3");
        return false;
    }

    public static void createBrokerInstrumentTransaction(int instrumentID, int brokerID, int quantity, int marketID,
            boolean isBuy, int price) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                stmt.setQueryTimeout(30);

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
                stmt.setQueryTimeout(30);

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
        System.out.println("If this ever happens I will be very surpised... 4");
        return -1;
    }

    public static int getNewQuantity(int instrumentID, int brokerID, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30);

                ResultSet rs = stmt.executeQuery("Select * from MarketBroker_" + marketID + " where InstrumentID = "
                        + instrumentID + " AND BrokerID = " + brokerID);
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
        System.out.println("If this ever happens I will be very surpised... 5");
        return -1;
    }

    public static boolean updateMarket(int instrumentID, int quantity, Boolean isBuy, int marketID, int brokerID) {
        Connection connection = null;
        try {
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

    public static boolean checkBrokerTransaction(int brokerID, int instrumentID, int marketID, boolean isBuy, int quantity) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int brokerQuantity = 0;
                stmt.setQueryTimeout(30);

                ResultSet rs = stmt.executeQuery("Select * from MarketBroker_" + marketID + " where BrokerID = "
                        + brokerID + " AND InstrumentID = " + instrumentID);
                while (rs.next()) {
                    count++;
                    brokerQuantity = rs.getInt("Quantity");
                }
                if (count == 1) {
                    if(!isBuy && brokerQuantity < quantity) {
                        return false;
                    }
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
        System.out.println("If this ever happens I will be very surpised... 6");
        return false;
    }

    public static int getNewMarketQuantity(int instrumentID, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30);

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
        System.out.println("If this ever happens I will be very surpised... 7");
        return -1;
    }

    public static int getInstrumentID(String instrument, int marketID) {
        Connection connection = null;
        try {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:Fixme.db");
                    Statement stmt = conn.createStatement()) {
                int count = 0;
                int ret = 0;
                stmt.setQueryTimeout(30);

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
        System.out.println("If this ever happens I will be very surpised... 8");
        return -1;
    }
}
