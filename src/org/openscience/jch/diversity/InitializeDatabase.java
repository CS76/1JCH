/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;
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

    public void generateMACCSKey() throws SQLException {
        int noOfRows = this.getRowCount("completeDataSet");
        int batchCount = (int) Math.ceil(noOfRows / 1000.0);
        int start, stop;
        start = 1;
        Statement stmt = this.connection.createStatement();
        Map< Integer, String> map = new HashMap< Integer, String>();
        ForkJoinPool fjPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        for (int k = 1; k <= 1000; k++) {
            System.out.println(k);
            stop = batchCount * k;
            map.clear();
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM completeDataSet WHERE ID>=" + start + " and ID<=" + stop + ";")) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String smi = rs.getString("SMILES");
                    map.put(id, smi);
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            updateMACCSEntries(fjPool.invoke(new MACCSKeyGenerator(map)));
            start = stop;
            map.clear();
            System.out.println("Done");
        }
    }

    private void updateMACCSEntries(Map<Integer, byte[]> mp) throws SQLException {
        String sql = "UPDATE completeDataSet SET FINGERPRINT = ? WHERE ID = ?";
        PreparedStatement psUpdateRecord = this.connection.prepareStatement(sql);
        int[] iNoRows = null;
        for (int a : mp.keySet()) {
            psUpdateRecord.setBytes(1, mp.get(a));
            psUpdateRecord.setInt(2, a);
            psUpdateRecord.addBatch();
        }
        iNoRows = psUpdateRecord.executeBatch();
        this.connection.commit();
    }

    public double getDiversity(byte[] mol1, byte[] mol2) throws CDKException {
        double diversity = 0.0;
        diversity = 1.0 - (Tanimoto.calculate(GeneralUtility.fromByteArray(mol1), GeneralUtility.fromByteArray(mol2)));
        return diversity;
    }

    public double getMaxSum(int molID) throws CDKException {
        double maxSum = 0.0;
        DataObject tempQuery = getDataObject(molID, "completeDataSet");
        DataObject[] dsHolder = getDataObject("diverseSubSet");
        int number = dsHolder.length;
        for (DataObject obj : dsHolder) {
            maxSum += getDiversity(tempQuery.getFp(), obj.getFp());
        }
        return maxSum / number;
    }

    public double getMaxMin(int molID) throws CDKException {
        double maxMin = Double.MAX_VALUE;
        DataObject tempQuery = getDataObject(molID, "completeDataSet");
        DataObject[] dsHolder = getDataObject("diverseSubSet");
        int number = dsHolder.length;
        for (DataObject obj : dsHolder) {
            double div = getDiversity(tempQuery.getFp(), obj.getFp());
            if (maxMin > div) {
                maxMin = div;
            }
        }
        return maxMin;
    }

    public DataObject[] getDataObject(String table) {
        DataObject[] tempObjArray = new DataObject[getRowCount(table)];
        Statement stmt = null;
        try {
            int f = 0;
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";");
            while (rs.next()) {
                DataObject tempObj = new DataObject(rs.getInt("ID"), rs.getString("SMILES"), rs.getBytes("FINGERPRINT"));
                tempObjArray[f] = tempObj;
                f += 1;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tempObjArray;
    }

    public DataObject getDataObject(int id, String table) {
        DataObject tempObj = null;
        Statement stmt = null;
        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE `ID` =" + id);
            while (rs.next()) {
                tempObj = new DataObject(rs.getInt("ID"), rs.getString("SMILES"), rs.getBytes("FINGERPRINT"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tempObj;
    }

    public void copyRow(String fromTable, String toTable, int orderID) {
        String sqlQuery = "INSERT INTO " + toTable + " SELECT * FROM " + fromTable + " WHERE ID = " + orderID + ";";
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

    public void copyRows(String fromTable, String toTable, int[] orderID) {
        String listOfID = GeneralUtility.conCat(orderID);
        String sqlQuery = "INSERT INTO " + toTable + " SELECT * FROM " + fromTable + " WHERE IN " + listOfID + ";";
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
    /**
     *
     */
}
