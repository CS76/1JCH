/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomTypeException;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {

    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException, CDKException, NoSuchAtomTypeException, CloneNotSupportedException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // Diverse subset selection
//         InitializeDatabase id = new InitializeDatabase("");
//         id.populateStructureData("/Users/chandu/Desktop/diversity/SPPassed.smi");
//         id.generateMACCSKey();
//         id.randomizeTable("completeDataSet");
//         id.OptiSim();
//         id.exportData("diverseSubSet", "/Users/chandu/Desktop/diversity/SP/SP_diverseSubset.txt");

        // Openbabel 3d coordinates(SDF) generation
//        List<String> smilesList = GeneralUtility.readLines("/Users/chandu/Desktop/filter/SP_diverseSubset.smi");
//        final long startTime = System.currentTimeMillis();
//        int count = 0;
//        for (String s : smilesList) {
//            count++;
//            GeneralUtility.appendToFile(ChemUtility.execOBgen(s)[0],"/Users/chandu/Desktop/filter/SP_diverseSubSet.sdf");
//        }
//        final long endTime = System.currentTimeMillis();
//
//        System.out.println("total exec time for "+count+"molecules:"+ (endTime - startTime));

        // Diverse subset diversity analysis
        DiversityAnalyser da = new DiversityAnalyser();
        da.analyseWithInDataSet("/Users/chandu/Desktop/try.smi", true);
    }

   
}
