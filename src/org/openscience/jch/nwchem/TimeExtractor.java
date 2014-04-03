/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author CS76
 */
public class TimeExtractor {

    public static void main(String[] args) throws FileNotFoundException, IOException, CDKException {
        String sourceFolderPath = "C:\\Users\\CS76\\Desktop\\dataExtracted\\SP2_SP_extracted\\";
        String targetFolderPath = "C:\\Users\\CS76\\Desktop\\dataExtracted\\nwChem_GeoOpt_MPA\\extractedGeoOptMPAData\\SP2_SP\\";
        List<String> fileNames = GeneralUtility.getAllFolderNamesInFolder(sourceFolderPath);
        StringBuilder mainSB = new StringBuilder();
        for (String s : fileNames) {
            String JCHFilePath = sourceFolderPath + s + "\\output.txt";
            String MPAFilePath = targetFolderPath + s + "\\output.txt";
            File f1 = new File(JCHFilePath);
            File f2 = new File(MPAFilePath);
            String time = s + "," + getTime(JCHFilePath) + "," + getTime(MPAFilePath);
            System.out.println(time);
            mainSB.append(time).append("\n");
        }
        GeneralUtility.writeToTxtFile(mainSB.toString(),"C:\\Users\\CS76\\Desktop\\SP2_SP.csv");
    }

    public static String getTime(String FilePath) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(FilePath));
            String line = br.readLine();
            List<String> finalData = new ArrayList<String>();
            while (line != null) {
                if (line.contains("cpu")) {
                    String[] lineData = line.split(" ");
                    for (int i = 0; i < lineData.length; i++) {
                        if (lineData[i].length() > 0) {
                            if (lineData[i].contains(".")) {
                                sb.append(lineData[i]).append(",");
                                break;
                            }
                        }
                    }

                }
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(TimeExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
}
