package org.openscience.jch.diversity;

import java.sql.*;

public class Sqlite {

//    public static void main(String[] args) throws ClassNotFoundException {
//        Connection c = connectDb("test.db");
//        String sql = "CREATE TABLE IF NOT EXISTS COMPANY "
//                + "(ID INT PRIMARY KEY     NOT NULL,"
//                + " NAME           TEXT    NOT NULL, "
//                + " AGE            INT     NOT NULL, "
//                + " ADDRESS        CHAR(50), "
//                + " SALARY         REAL)";
//
//        //createTable(c, sql);
//
//        //String sqlInsert = "INSERT INTO COMPANY (ID,NAME,AGE,ADDRESS,SALARY) "
//        //        + "VALUES (2, 'Paul', 32, 'California', 20000.00 );";
//
//        //insertInto(c, sqlInsert);
//        
//        String sqlSelect = "SELECT * FROM COMPANY;";
//        
//        select(c,sqlSelect);
//    }


    public void execQuery(String sqlQuery,Connection c) {
        Connection tempConnection =c;
        Statement stmt = null;
        try {
            stmt = tempConnection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            tempConnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
//        try {
//            stmt = tempConnection.createStatement();
//            ResultSet rs = stmt.executeQuery(sqlQuery);
//            while (rs.next()) {
//                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
//                    System.out.print(rs.getString(i)+"    ");
//                }
//                System.out.println("");
//            }
//            rs.close();
//            stmt.close();
//            tempConnection.close();
//        } catch (Exception e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//            System.exit(0);
//        }
        System.out.println("Operation done successfully");

    }

    public void select(String sqlQuery, Connection x) {
        Connection tempConnection = connectDb("Zinc.db");
        Statement stmt = null;
        try {
            stmt = tempConnection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM MOLDATA;" );
            while (rs.next()) {
                
                int id = rs.getInt("ID");
                String name = rs.getString("STUCTURE");
                System.out.println("hie");
                String smiles = rs.getString("SMILES");
                String bit =rs.getString("MACCKEY");
                System.out.print("ID = " + id);
                System.out.println("in the loop");
                System.out.print("MOLTABLE /n"+name);
                System.out.print("SMILES : "+smiles);
                System.out.println("MACCKEY" + bit);
            }
            rs.close();
            stmt.close();
            tempConnection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }

    public void insertInto(String sqlQuery,Connection c) {
        try {
            c.setAutoCommit(false);
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }

    public void createTable(String sqlQuery,Connection c) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Table successfully created");
    }

    public Connection connectDb(String path) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }
}