package org.openscience.jch.diversity;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author chandu
 */
public class OPTISIMDiversity {

    private int k = 0;
    private double dissimilarity = 0.0;
    private int diverseSubSetSize = 0;
    private Initialize initializedObject = null;
    //private initialize ini = null;

    public OPTISIMDiversity(String structuresFilePath, int k, double dissim, int subSetSize, Initialize initialization) throws SQLException {
        if (initialization != null){
            this.initializedObject = initialization;
        }else{
            this.initializedObject = new Initialize("default.db", false);
        }
         
        this.dissimilarity = dissim;
        this.diverseSubSetSize = subSetSize;
        //this.ini = initialization;
        
        
        

    }
}
