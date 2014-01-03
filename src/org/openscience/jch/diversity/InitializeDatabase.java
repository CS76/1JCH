/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.jch.filter.ParameterEngine;
import org.openscience.jch.utilities.IteratingMolTableReader;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class InitializeDatabase {

    private String database = "ChemDB.db";
    private String workingDirectory;
    private String dataBasePath;
    private String StructuresFilePath;
    private Connection connection = null;
    private boolean clearContent = false;
    private final String[] tableNames = {"completeDataSet", "kSubSet", "recycleSet", "diverseSubSet"};

    /**
     * Constructor 1
     *
     * @param workingDirectoryPath
     * @param structureFilePath
     */
    public InitializeDatabase(String workingDirectoryPath) throws SQLException {
        this(workingDirectoryPath, false);
    }

    /**
     * Constructor 2
     *
     * @param workingDirectoryPath
     * @param structureFilePath
     */
    public InitializeDatabase(String workingDirectoryPath, boolean ClearContents) throws SQLException {
        this.workingDirectory = workingDirectoryPath;
        this.dataBasePath = "ChemDB.db";
        this.clearContent = ClearContents;
        intitialize();
    }

    private void intitialize() throws SQLException {
        //File existence check
        File file = new File("ChemDb.db");

        // Clearing  / !Clearing content and then establishing connection
        if (file.exists()) {
            if (this.clearContent) {
                if (file.delete()) {
                    System.out.println(file.getName() + "Tables are cleared");
                } else {
                    System.out.println("Data Clearing failed.");
                }
                System.out.print("Reconnecting to the database: " + this.database);
                this.connection = connectDb(this.database);
            } else {
                System.out.print("This database already exists; Establishing Connection: " + this.dataBasePath);
                this.connection = connectDb(this.database);
            }
        } else {
            System.out.print("This database dont not exist; Creating a new database: " + this.dataBasePath);
            System.out.println("Establishing connection: " + this.dataBasePath);
            this.connection = connectDb(this.database);
        }

        generateTables();
        updateMaster();
    }

    private void generateTables() throws SQLException {
        if (tableCheck("MASTER")) {
            System.out.println("");
            // create the tables if !exists
            for (String name : this.tableNames) {
                if (!tableCheck(name)) {
                    createTable(name);
                }
            }
            this.connection.setAutoCommit(false);
            this.updateMaster();
        } else {
            createTable("MASTER");
            for (String name : this.tableNames) {
                if (!tableCheck(name)) {
                    createTable(name);
                }
            }
            this.connection.setAutoCommit(false);
            this.populateMaster();
        }
    }

    public void populateStructureData(String structureSmilesFilePath) throws FileNotFoundException, IOException, SQLException {
        System.out.println("In data importer");

        this.StructuresFilePath = structureSmilesFilePath;
        int ID = getRowCount("completeDataSet");
        int count = 0;

        BufferedReader br = new BufferedReader(new FileReader(this.StructuresFilePath));
        List<DataObject> tempDataHolder = new ArrayList<DataObject>();
        DataObject dO = new DataObject();

        String line = br.readLine();
        while (line != null) {
            //System.out.println(line);
            ID++;
            count += 1;
            dO = new DataObject();
            dO.setID(ID);
            dO.setSmiles(line.split(" ")[0]);
            tempDataHolder.add(dO);
            if (count == 10000) {
                System.out.println(ID);
                // code to execute the screening
                ID = insertSMILESInToTable(tempDataHolder, ID);
                count = 0;
                tempDataHolder.clear();
            }
            line = br.readLine();
        }
        if (count != 0) {
            ID = insertSMILESInToTable(tempDataHolder, ID);
            count = 0;
            tempDataHolder.clear();
        }
    }
    
    public void generateMACCSKey(){
        
    }
    
    
    
    
    
    
    /**
     * **
     * Utility methods * * *
     */
    /**
     * Method to establish connection to the database
     *
     * @param databasePath: string path to the database
     * @return Connection object
     */
    private Connection connectDb(String databasePath) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        return c;
    }

    /**
     * Checks if table exists or !exists
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    private boolean tableCheck(String tableName) throws SQLException {
        DatabaseMetaData dbm = this.connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            return true;
        }
        return false;
    }

    /**
     * creates a table
     *
     * @param tableName
     */
    private void createTable(String tableName) {
        String createTableQuery = "";
        Statement stmt = null;
        if (tableName == "MASTER") {
            createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " "
                    + "(TABLE_ID INT PRIMARY KEY     NOT NULL,"
                    + " NAME    VARCHAR(24)     NOT NULL, "
                    + " ROWS     INT)";
        } else {
            createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + " "
                    + "(ID INT PRIMARY KEY     NOT NULL,"
                    + " STRUCTURE    CLOB, "
                    + " SMILES      CLOB,"
                    + "FINGERPRINT   BLOB)";
        }
        try {
            stmt = this.connection.createStatement();
            stmt.executeUpdate(createTableQuery);
            stmt.close();
            System.out.println("Table" + tableName + " successfully created");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Populates Master Table
     *
     * @throws SQLException
     */
    private void populateMaster() throws SQLException {
        String insertQuery = "INSERT INTO MASTER (TABLE_ID, NAME, ROWS) VALUES (?,?,?)";
        PreparedStatement insertRecord = connection.prepareStatement(insertQuery);
        int[] iNoRows = null;
        for (int i = 0; i < this.tableNames.length; i++) {
            System.out.println(i + 1 + "....");
            String name = this.tableNames[i];
            insertRecord.setInt(1, i + 1);
            insertRecord.setString(2, name);
            insertRecord.setInt(3, getRowCount(name));
            insertRecord.addBatch();
        }
        iNoRows = insertRecord.executeBatch();
        connection.commit();
    }

    /**
     * Updates Master Table
     *
     * @throws SQLException
     */
    private void updateMaster() throws SQLException {
        String updateQuery = "UPDATE MASTER SET ROWS = ? WHERE NAME = ? ";
        PreparedStatement psUpdateRecord = connection.prepareStatement(updateQuery);
        int[] iNoRows = null;
        for (String name : this.tableNames) {
            psUpdateRecord.setInt(1, getRowCount(name));
            psUpdateRecord.setString(2, name);
            psUpdateRecord.addBatch();
        }
        iNoRows = psUpdateRecord.executeBatch();
        connection.commit();
    }

    public String[] getTablesList() {
        return this.tableNames;
    }

    public Connection getConnection() {
        return this.connection;
    }

    /**
     * returns the number of rows in the table if table !exits throws exception
     *
     * @param tableName
     * @return
     */
    public int getRowCount(String tableName) {
        int noOfRows = 0;
        try {
            Statement st = this.connection.createStatement();
            ResultSet res = st.executeQuery("SELECT COUNT(*) FROM " + tableName);
            while (res.next()) {
                noOfRows = res.getInt(1);
            }
            st.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return noOfRows;
    }

    /**
     * returns true if table exists else returns false
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData dbm = this.connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param co
     * @param count
     * @return
     * @throws SQLException
     */
    public int insertSMILESInToTable(List<DataObject> co, int count) throws SQLException {
        String sqlInsertRecord = "INSERT INTO completeDataSet(ID, SMILES) values(?,?)";
        int i = count;
        PreparedStatement psInsertRecord = connection.prepareStatement(sqlInsertRecord);
        int[] iNoRows = null;

        for (DataObject obj : co) {
            psInsertRecord.setInt(1, i);
            psInsertRecord.setString(2, obj.getSmiles());
            psInsertRecord.addBatch();
            i++;
        }
        iNoRows = psInsertRecord.executeBatch();
        connection.commit();
        return i;
    }

    /**
     *
     * @param fromTable
     * @param toTable
     */
    public void copyTable(String fromTable, String toTable) {
        String sqlQuery = "INSERT INTO " + toTable + " SELECT * FROM " + fromTable + ";";
        try {
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            this.connection.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records transferred successfully");
    }
}
