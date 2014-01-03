package org.openscience.jch.diversity2222;

import java.sql.SQLException;

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
        if (initialization != null) {
            this.initializedObject = initialization;
        } else {
            this.initializedObject = new Initialize("default.db", false);
        }
        this.dissimilarity = dissim;
        this.diverseSubSetSize = subSetSize;
        //this.ini = initialization;
    }
}
