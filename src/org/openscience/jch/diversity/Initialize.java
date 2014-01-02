/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.jch.utilities.GeneralUtility;
import org.openscience.jch.utilities.IteratingMolTableReader;
import org.openscience.jch.utilities.IteratingMolTableReader.CustomObject;

public class Initialize {

    private String database = "";
    private Connection connection = null;
    private boolean clearTables = false;
    private final String[] dbNames = {"completeDataSet", "kSubSet", "recycleSet", "diverseSubSet"};

    Initialize(String databasePath, boolean clear) throws SQLException {
        this.database = databasePath;
        this.clearTables = clear;
        File file = new File(this.database);
        /**
         * checks if the db file exists / !exists. If db exists If the clear
         * tables is set true drops of all the tables and creates new tables
         * else connects to the database else creates a new database and the
         * creates a new set of tables
         */
        if (file.exists()) {
            if (this.clearTables) {
                if (file.delete()) {
                    System.out.println(file.getName() + "tables are cleared");
                } else {
                    System.out.println("Delete operation is failed.");
                }
                System.out.print("Reconnecting to the database: " + databasePath);
                this.connection = connectDb(this.database);
            } else {
                System.out.print("This database already exists; Connecting to the database: " + databasePath);
                this.connection = connectDb(this.database);
            }
        } else {
            System.out.print("This database dont not exist; Creating a new database: " + databasePath);
            this.connection = connectDb(this.database);
        }
        generateTables();
        
        //updateMaster();
        System.out.println("exiting initialize");
    }

    private void generateTables() throws SQLException {
        /**
         * if (master exist){ if child tables exist{ do nothing } else{ create
         * child tables and register them in the master } } else{ create master
         * and then the create child tables, register then in the master table }
         */
        if (tableCheck("MASTER")) {
            // create the tables if !exists
            for (String name : this.dbNames) {
                if (!tableCheck(name)) {
                    createTable(name);
                }
            }
        } else {
            createTable("MASTER");
            for (String name : this.dbNames) {
                if (!tableCheck(name)) {
                    createTable(name);
                }
            }
        }
        this.connection.setAutoCommit(false);
        //insertMaster();
    }

    private void insertMaster() throws SQLException {

        String insertQuery = "INSERT INTO MASTER (TABLE_ID, NAME, ROWS) VALUES (?,?,?)";
        PreparedStatement insertRecord = connection.prepareStatement(insertQuery);
        int[] iNoRows = null;
        for (int i = 0; i < this.dbNames.length; i++) {
            System.out.println(i + 1 + "....");
            String name = this.dbNames[i];
            insertRecord.setInt(1, i + 1);
            insertRecord.setString(2, name);
            insertRecord.setInt(3, getRowCount(name));
            insertRecord.addBatch();
        }
        iNoRows = insertRecord.executeBatch();
        connection.commit();
    }

    private void updateMaster() throws SQLException {
        String updateQuery = "UPDATE MASTER SET ROWS = ? WHERE NAME = ? ";
        PreparedStatement psUpdateRecord = connection.prepareStatement(updateQuery);
        int[] iNoRows = null;
        for (String name : this.dbNames) {
            psUpdateRecord.setInt(1, getRowCount(name));
            psUpdateRecord.setString(2, name);
            psUpdateRecord.addBatch();
        }
        iNoRows = psUpdateRecord.executeBatch();
        connection.commit();
    }

    public String[] getTablesList() {
        return this.dbNames;
    }

    public Connection getConnection() {
        return this.connection;
    }

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
                    + " STUCTURE    CLOB     NOT NULL, "
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

    public int insertInToTable(List<CustomObject> co, int count) throws SQLException {
        String sqlInsertRecord = "INSERT INTO completeDataSet(ID, STUCTURE) values(?,?)";
        int i = count;
        PreparedStatement psInsertRecord = connection.prepareStatement(sqlInsertRecord);
        int[] iNoRows = null;

        for (CustomObject obj : co) {
            psInsertRecord.setInt(1, i);
            psInsertRecord.setString(2, obj.getMolTable());
            psInsertRecord.addBatch();
            i++;
        }
        iNoRows = psInsertRecord.executeBatch();
        connection.commit();
        return i;
    }

    private boolean tableCheck(String tableName) throws SQLException {
        DatabaseMetaData dbm = this.connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            return true;
        }
        return false;
    }

    /**
     * Method to connect to the database
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

    public void generateSmiles() throws SQLException {

        int noOfRows = this.getRowCount("completeDataSet");
        int batchCount = (int) Math.ceil(noOfRows / 10.0);

        int start, stop;
        start = 1;
        Statement stmt = this.connection.createStatement();
        Map< Integer, String> map = new HashMap< Integer, String>();
        SmilesGenerator sg = new SmilesGenerator();
        IAtomContainer molecule = null;

        for (int k = 1; k <= 10; k++) {
            stop = batchCount * k;
            map.clear();
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM completeDataSet WHERE ID>=" + start + " and ID<=" + stop + ";")) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    InputStream is = new ByteArrayInputStream(rs.getString("STUCTURE").getBytes());
                    IteratingMolTableReader reader = new IteratingMolTableReader(is, DefaultChemObjectBuilder.getInstance(), true);
                    while (reader.hasNext()) {
                        molecule = reader.next();
                        break;
                    }
                    System.out.println(id);
                    map.put(id, sg.createSMILES(molecule));
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            updateSMILESEntries(map);
            start = stop;
            map.clear();
        }
    }

    public void updateSMILESEntries(Map< Integer, String> mp) throws SQLException {
        String sql = "UPDATE completeDataSet SET SMILES = ? WHERE ID = ?";
        PreparedStatement psUpdateRecord = this.connection.prepareStatement(sql);
        int[] iNoRows = null;
        for (int a : mp.keySet()) {
            psUpdateRecord.setString(1, mp.get(a));
            psUpdateRecord.setInt(2, a);
            psUpdateRecord.addBatch();
        }
        iNoRows = psUpdateRecord.executeBatch();
        this.connection.commit();
    }

    public void generateMACCSKeys() throws SQLException {
        int noOfRows = this.getRowCount("completeDataSet");
        int batchCount = (int) Math.ceil(noOfRows / 100.0);

        int start, stop;
        start = 1;
        Statement stmt = this.connection.createStatement();
        Map< Integer, byte[]> map = new HashMap< Integer, byte[]>();

        MACCSFingerprinter mp = new MACCSFingerprinter();

        IAtomContainer molecule = null;
        for (int k = 1; k <= 100; k++) {
            stop = batchCount * k;
            map.clear();
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM completeDataSet WHERE ID>=" + start + " and ID<=" + stop + ";")) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    InputStream is = new ByteArrayInputStream(rs.getString("STUCTURE").getBytes());
                    IteratingMolTableReader reader = new IteratingMolTableReader(is, DefaultChemObjectBuilder.getInstance(), true);
                    while (reader.hasNext()) {
                        molecule = reader.next();
                        break;
                    }
                    System.out.println(id);
                    byte[] bi = mp.getBitFingerprint(molecule).asBitSet().toByteArray();
                    map.put(id, bi);
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            updateMACCSEntries(map);
            start = stop;
            map.clear();
        }

    }

    public void updateMACCSEntries(Map< Integer, byte[]> mp) throws SQLException {
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

    public void deleteOriginal(String fromTable, int id) {
        String sqlQuery = "DELETE FROM " + fromTable + " WHERE `ORDER` =" + id + ";";
        try {
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            this.connection.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records removed successfully");
    }
    
    public void deleteAllRows(String fromTable) {
        String sqlQuery = "DELETE FROM " + fromTable+ ";";
        try {
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            this.connection.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Records removed successfully ========================================");
    }

    public TableObject getTableObject(int id, String table) {
        TableObject tempObj = null;
        Statement stmt = null;
        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE `ID` =" + id);
            while (rs.next()) {
                tempObj = new TableObject(rs.getInt("ID"), rs.getString("STUCTURE"), rs.getString("SMILES"), rs.getBytes("FINGERPRINT"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return tempObj;
    }

    public TableObject[] getTableObject(String table) {

        TableObject[] tempObjArray = new TableObject[getRowCount(table)];
        Statement stmt = null;
        try {
            int f = 0;
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + ";");
            while (rs.next()) {
                TableObject tempObj = new TableObject(rs.getInt("ID"), rs.getString("STUCTURE"), rs.getString("SMILES"), rs.getBytes("FINGERPRINT"));
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

    public double getDiversity(byte[] mol1, byte[] mol2) throws CDKException {
        double diversity = 0.0;
        diversity = 1.0 - (Tanimoto.calculate(GeneralUtility.fromByteArray(mol1), GeneralUtility.fromByteArray(mol2)));
        return diversity;
    }

    public double getMaxSum(int molID) throws CDKException {
        double maxSum = 0.0;
        TableObject tempQuery = getTableObject(molID, "completeDataSet");
        TableObject[] dsHolder = getTableObject("diverseSubSet");
        int number = dsHolder.length;
        for (TableObject obj : dsHolder) {
            maxSum += getDiversity(tempQuery.getFp(), obj.getFp());
        }
        return maxSum / number;

    }

    public double getMaxMin(int molID) throws CDKException {
        double maxMin = Double.MAX_VALUE;
        TableObject tempQuery = getTableObject(molID, "completeDataSet");
        TableObject[] dsHolder = getTableObject("diverseSubSet");
        int number = dsHolder.length;
        for (TableObject obj : dsHolder) {
            double div = getDiversity(tempQuery.getFp(), obj.getFp());
            if (maxMin > div) {
                maxMin = div;
            }
        }
        return maxMin;
    }

    public class TableObject {

        private int ID = 0;
        private IAtomContainer mol = null;
        private String smiles = "";
        private byte[] MACCSKEYS = null;
        private String molTable = "";

        TableObject(int molID, String connecTable, String Smiles, byte[] fp) {
            this.ID = molID;
            this.MACCSKEYS = fp;
            this.smiles = Smiles;
            this.molTable = connecTable;
        }

        public int getID() {
            return this.ID;
        }

        public String getSmiles() {
            return this.smiles;
        }

        public byte[] getFp() {
            return this.MACCSKEYS;
        }

        public IAtomContainer getMolecule() {
            return this.mol;
        }

        public String getMolTable() {
            return this.molTable;
        }
    }

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