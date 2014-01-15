/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.jch.utilities.GeneralUtility;

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
    private final String[] tableNames = {"completeDataSet", "kSubSet", "recycleSet", "diverseSubSet", "randomCompleteDataSet"};

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
        int ID = 0;
        int count = 0;
        BufferedReader br = new BufferedReader(new FileReader(this.StructuresFilePath));
        List<DataObject> tempDataHolder = new ArrayList<DataObject>();
        DataObject dO = new DataObject();

        String line = br.readLine();
        while (line != null) {
            String[] molData = line.split(" ");
            ID++;
            count += 1;
            dO = new DataObject();
            dO.setID(ID);
            dO.setCustomID1(Integer.valueOf(molData[1]));
            dO.setCustomID2(Integer.valueOf(molData[2]));
            dO.setUserData(line);
            dO.setSmiles(molData[0]);
            tempDataHolder.add(dO);
            if (count == 1000) {
                System.out.println(ID);
                // code to execute the screening
                insertSMILESInToTable(tempDataHolder);
                count = 0;
                tempDataHolder.clear();
            }
            line = br.readLine();
        }
        if (count != 0) {
            insertSMILESInToTable(tempDataHolder);
            count = 0;
            tempDataHolder.clear();
        }
    }

    public void generateMACCSKey() throws SQLException {
        int noOfRows = this.getRowCount("completeDataSet");
        int batchCount = (int) Math.ceil(noOfRows / 10.0);
        int start, stop;
        start = 1;
        Statement stmt = this.connection.createStatement();
        TreeMap< Integer, String> map = new TreeMap< Integer, String>();
        ForkJoinPool fjPool = new ForkJoinPool(2);
        for (int k = 1; k <= 10; k++) {
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

    public void OptiSim(int kSSSize,double threshold, int dSSSize) throws CDKException {
        int kSubsetSize = kSSSize;
        double diversityThreshold = threshold;
        int diverseSetSize = dSSSize;
        
        int randomCompleteDataSetCount = getRowCount("randomCompleteDataSet");
        int kSubSetCount = getRowCount("kSubSet");
        int diverseSubSetCount = getRowCount("diverseSubSet");
        int recycleSetCount = getRowCount("recycleSet");;
        
        System.out.println("randomCompleteDataSet Count:" + randomCompleteDataSetCount);
        System.out.println("kSubSet Count:" + kSubSetCount);
        System.out.println("diverse SubSet Count:" + diverseSubSetCount);
        System.out.println("recycle SubSet Count:" + recycleSetCount);
      
        // initialize the sub set
        copyRow("randomCompleteDataSet", "diverseSubSet", 1);
        diverseSubSetCount++;
        deleteRow("randomCompleteDataSet", 1);
        randomCompleteDataSetCount--;
        System.out.println("intialized");
        
        while(randomCompleteDataSetCount != 0  || recycleSetCount !=0){
            System.out.println("Enter While:"+ randomCompleteDataSetCount + "," + recycleSetCount);
            if (diverseSubSetCount >= diverseSetSize) {
                System.out.println("Target Diverse Sub Set reached Exiting the l]Loop");
                break;
            }
            
            if ((randomCompleteDataSetCount == 0) && recycleSetCount !=0) {
                System.out.println("randomCompleteDataSet is exhausted, copying data from recycle set");
                copyTable("recycleSet", "randomCompleteDataSet");
                deleteAllRows("recycleSet");
                randomCompleteDataSetCount = getRowCount("randomCompleteDataSet");
                recycleSetCount = 0;
            }
            
            double[] diversityData = new double[2];
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            
            while (map.size() <= kSubsetSize) {
                if (randomCompleteDataSetCount != 0) {
                    try {
                        double tempDiversity = 0.0;
                        //diversityData = getMaxSum(presentCount);
                        diversityData = getMaxMin(1);
                        tempDiversity = diversityData[0];
                        
                        if (tempDiversity <= diversityThreshold) {
                            deleteRow("randomCompleteDataSet", (int) diversityData[1]);
                            randomCompleteDataSetCount--;
                            System.out.println(randomCompleteDataSetCount);
                        } else {
                            map.put((int) diversityData[1], tempDiversity);
                            copyRow("randomCompleteDataSet", "kSubSet", (int) diversityData[1]);
                            deleteRow("randomCompleteDataSet", (int) diversityData[1]);
                            randomCompleteDataSetCount--;
                            kSubSetCount++;
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("Error processing the mol: FP Not Generated");
                    }
                } else {
                    System.out.println("RandomSubSet exhausted");
                    break;
                }
            }
            
            double largestDiversity = Double.MIN_VALUE;
            int largeDivMolID = 0;
            for (int id : map.keySet()) {
                if (map.get(id) > largestDiversity) {
                    largestDiversity = map.get(id);
                    largeDivMolID = id;
                }
            }
            copyRow("kSubSet", "diverseSubSet", largeDivMolID);
            diverseSubSetCount++;
            map.remove(largeDivMolID);
            for (int div : map.keySet()) {
                copyRow("kSubSet", "recycleSet", div);
                recycleSetCount++;
            }
            deleteAllRows("kSubSet");
            kSubSetCount = 0;
            map.clear();
        }

//        while (presentCount <= randomCompleteDataSetCount) {
//            System.out.println(presentCount + "," + getRowCount("randomCompleteDataSet") + "," + diverseSubSetCount + "±±±±±±±±±±±±±±±±±±±±±±±±±±±±±");
//            if (diverseSubSetCount >= diverseSetSize) {
//                System.out.println("cond1");
//                break;
//            }
//            if (getRowCount("randomCompleteDataSet") == 0 && getRowCount("recycleSet") == 0) {
//                System.out.println("cond1");
//                break;
//            }
//            if ((randomCompleteDataSetCount == presentCount)) {
//                System.out.println("cond2");
//                copyTable("recycleSet", "randomCompleteDataSet");
//                presentCount = 1;
//                deleteAllRows("recycleSet");
//                randomCompleteDataSetCount = getRowCount("randomCompleteDataSet");
//            }
//            System.out.println(getRowCount("randomCompleteDataSet") + "," + getRowCount("recycleSet"));
//            double[] diversityData = new double[2];
//            Map<Integer, Double> map = new HashMap<Integer, Double>();
//            while (map.size() < kSubsetSize) {
//                System.out.println(presentCount + "," + getRowCount("randomCompleteDataSet")+ "," + diverseSubSetCount + "**********************");
//                if (presentCount < randomCompleteDataSetCount) {
//                    try {
//                        double tempDiversity = 0.0;
//                        //diversityData = getMaxSum(presentCount);
//                        diversityData = getMaxMin(presentCount);
//                        tempDiversity = diversityData[0];
//                        if (tempDiversity <= diversityThreshold) {
//                            deleteRow("randomCompleteDataSet", (int) diversityData[1]);
//                            presentCount += 1;
//                        } else {
//                            map.put((int) diversityData[1], tempDiversity);
//                            copyRow("randomCompleteDataSet", "kSubSet", (int) diversityData[1]);
//                            deleteRow("randomCompleteDataSet", (int) diversityData[1]);
//                            presentCount += 1;
//                        }
//                    } catch (Exception e) {
//                        System.out.println(e.getMessage());
//                        System.out.println("error processing the mol: FP Not Generated");
//                        presentCount += 1;
//                    }
//                } else {
//                    System.out.println("exiting");
//                    break;
//                }
//            }
//
//            if (getRowCount("randomCompleteDataSet") < kSubsetSize && getRowCount("recycleSet")<kSubsetSize) {
//                System.out.println("this is the tricky part");
//                int u = getRowCount("randomCompleteDataSet");
//                int v = 1;
//                while (v <= u) {
//                    double tempDiversity = 0.0;
//                    //diversityData = getMaxSum(presentCount);
//                    diversityData = getMaxMin(v);
//                    tempDiversity = diversityData[0];
//                    if (tempDiversity <= diversityThreshold) {
//                        deleteRow("randomCompleteDataSet", (int) diversityData[1]);
//                    } else {
//                        map.put((int) diversityData[1], tempDiversity);
//                        deleteRow("randomCompleteDataSet", (int) diversityData[1]);
//                    }
//                    v++;
//                }
//            }
//
//            //System.out.println("map:" + map.size() + "======" + kSubsetSize);
//            double largestDiversity = Double.MIN_VALUE;
//            int largeDivID = 0;
//            for (int id : map.keySet()) {
//                if (map.get(id) > largestDiversity) {
//                    largestDiversity = map.get(id);
//                    largeDivID = id;
//                }
//            }
//            copyRow("kSubSet", "diverseSubSet", largeDivID);
//            deleteRow("randomCompleteDataSet", largeDivID);
//            ++diverseSubSetCount;
//            map.remove(largeDivID);
//            for (int div : map.keySet()) {
//                copyRow("kSubSet", "recycleSet", div);
//            }
//            deleteAllRows("kSubSet");
//            map.clear();
//        }
    }

    public double getDiversity(byte[] mol1, byte[] mol2) throws CDKException {
        double diversity = 0.0;
        // System.out.println(GeneralUtility.fromByteArray(mol1).cardinality()+","+GeneralUtility.fromByteArray(mol2).cardinality());
        diversity = 1.0 - (Tanimoto.calculate(GeneralUtility.fromByteArray(mol1), GeneralUtility.fromByteArray(mol2)));
        return diversity;
    }

    public double[] getMaxSum(int molID) throws CDKException {
        double[] divData = {0.0, 0.0};
        double maxSum = 0.0;

        DataObject tempQuery = getDataObject(molID, "randomCompleteDataSet");

        DataObject[] dsHolder = getDataObject("diverseSubSet");

        int number = dsHolder.length;
        for (DataObject obj : dsHolder) {
            maxSum += getDiversity(tempQuery.getFp(), obj.getFp());
        }
        //System.out.println(maxSum);

        divData[0] = maxSum / number;
        divData[1] = tempQuery.getID();
        return divData;
    }

    public double[] getMaxMin(int molID) throws CDKException {
        double maxMin[] = {0.0, 0.0};
        double diversity = Double.MAX_VALUE;
        DataObject tempQuery = getDataObject(molID, "randomCompleteDataSet");
        DataObject[] dsHolder = getDataObject("diverseSubSet");
        double id = 0.0;
        for (DataObject obj : dsHolder) {
            double div = getDiversity(tempQuery.getFp(), obj.getFp());
            if (div < diversity) {
                diversity = div;
            }
        }
        maxMin[0] = diversity;
        maxMin[1] = tempQuery.getID();
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
                DataObject tempObj = new DataObject(rs.getInt("ID"), rs.getString("SMILES"), rs.getBytes("FINGERPRINT"), rs.getString("USERDATA"));
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
        //System.out.println("in data Object1,"+ id+","+table);
        DataObject tempObj = null;
        Statement stmt = null;
        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " ORDER BY `rowid` ASC LIMIT 1;");
            while (rs.next()) {
                //System.out.println(rs.getInt("ID") + "," + rs.getString("SMILES"));
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
        System.out.println("Single Record transferred successfully");
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
                    + "FINGERPRINT   BLOB,"
                    + "CUSTOMID1 INT,"
                    + "CUSTOMID2 INT,"
                    + "USERDATA CLOB)";
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
    public void insertSMILESInToTable(List<DataObject> co) throws SQLException {
        String sqlInsertRecord = "INSERT INTO completeDataSet(ID, SMILES, CUSTOMID1, CUSTOMID2, USERDATA) values(?,?,?,?,?)";
        PreparedStatement psInsertRecord = connection.prepareStatement(sqlInsertRecord);
        int[] iNoRows = null;

        for (DataObject obj : co) {
            psInsertRecord.setInt(1, obj.getID());
            psInsertRecord.setString(2, obj.getSmiles());
            psInsertRecord.setInt(3, obj.getCustomID1());
            psInsertRecord.setInt(4, obj.getCustomID2());
            psInsertRecord.setString(5, obj.getUserData());
            psInsertRecord.addBatch();
        }
        iNoRows = psInsertRecord.executeBatch();
        connection.commit();
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
    public void randomizeTable(String Table) {
        System.out.println("random");
        String randomTable = "random" + Table;
        String sqlQuery = "INSERT INTO randomcompleteDataSet SELECT * FROM  completeDataSet ORDER BY RANDOM();";
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

    public void deleteAllRows(String fromTable) {
        String sqlQuery = "DELETE FROM " + fromTable + ";";
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

    public void deleteRow(String fromTable, int i) {
        String sqlQuery = "DELETE FROM " + fromTable + " WHERE `ID` =" + i + ";";
        try {
            Statement stmt = this.connection.createStatement();
            stmt.executeUpdate(sqlQuery);
            stmt.close();
            this.connection.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        //System.out.println("Records removed successfully ========================================");
    }

    public void exportData(String fromTable, String filePath) throws CDKException, IOException {
        DataObject[] doArray = getDataObject(fromTable);
        System.out.println("enetered export data," + doArray.length);
        List<String> as = new ArrayList<String>();
        for (DataObject d : doArray) {
            as.add(d.getUserData());
            System.out.println(d.getUserData());
            if (as.size() == 10000) {
                GeneralUtility.writeToTxtFile(GeneralUtility.getStringFromList(as), filePath);
                as.clear();
            }
        }
        if (as.size() != 0) {
            GeneralUtility.writeToTxtFile(GeneralUtility.getStringFromList(as), filePath);
            as.clear();
        }
    }
}
