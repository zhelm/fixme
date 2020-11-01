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
                    System.out.println("DATABASE SAYS : ");
                    try {
                        rs.getString("Instrument");
                        System.out.println(rs.getString("Instrument"));
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if(count == 1) {
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
                    System.out.println("DATABASE SAYS : ");
                    try {
                        Available_Quantity = rs.getInt("Available_Quantity");
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if(count == 1 && quantity <= Available_Quantity) {
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
                    System.out.println("DATABASE SAYS : ");
                    try {
                        price = rs.getInt("Price");
                        System.out.println("Price of Instrument: " + price);
                    } catch (Exception e) {
                        System.out.println("No such column found");
                    }
                }
                if(count == 1 && limit <= price) {
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

    public static void executeTransaction(String Instrument, int quantity, int brokerID, boolean isBuy) {

    }

}
