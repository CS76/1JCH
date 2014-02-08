/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException, CDKException, NoSuchAtomTypeException, CloneNotSupportedException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // Diverse subset selection
        InitializeDatabase id = new InitializeDatabase("");
//        id.populateStructureData("/Users/chandu/Desktop/diversity/SP2/SP2Passed.smi");
//        id.generateMACCSKey();
        //id.randomizeTable("completeDataSet");
      // id.OptiSim(10,0.50, 10000);
       //id.exportData("diverseSubSet", "/Users/chandu/Desktop/diversity/SP2/SP2_diverseSubset_maxmin.smi");

        // Openbabel 3d coordinates(SDF) generation
        List<String> smilesList = GeneralUtility.readLines("/Users/chandu/Desktop/diversity/SP2/SP2_diverseSubset_maxmin.smi");
        final long startTime = System.currentTimeMillis();
        int count = 0;
        for (String s : smilesList) {
            count++;
            GeneralUtility.appendToFile(ChemUtility.execOBgen(s)[0],"/Users/chandu/Desktop/diversity/SP2/SP2_diverseSubSet_maxmin.sdf");
                    }
        final long endTime = System.currentTimeMillis();
        System.out.println("total exec time for "+count+"molecules:"+ (endTime - startTime));

        // Diverse subset diversity analysis
      //  DiversityAnalyser da = new DiversityAnalyser();
       // da.analyseWithInDataSet("/Users/chandu/Desktop/diversity/SP2/SP2_diverseSubset_maxmin.smi", true);        
    }
}