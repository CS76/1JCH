/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.similarity.Tanimoto;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class DiversityAnalyser {

    String smilesSeparator = " ";

    public DiversityAnalyser() {
    }

    public DiversityAnalyser(String regexSplit) {
        this.smilesSeparator = regexSplit;
    }

    public double[][] analyseWithInDataSet(String pathToDataSet, boolean writeDataToFile) throws FileNotFoundException, IOException, CDKException {
        List<String> smilesData = GeneralUtility.readLines(pathToDataSet);
        DecimalFormat df = new DecimalFormat("#.####");
        final int dataSetSize = smilesData.size();
        boolean recordData = writeDataToFile;
        double[][] diversityMatrix = new double[dataSetSize][dataSetSize];
        MACCSFingerprinter mfp = new MACCSFingerprinter();
        for (int i = 0; i < dataSetSize; i++) {
            String iMol = smilesData.get(i).split(this.smilesSeparator)[0];
            IAtomContainer imol = ChemUtility.getIAtomContainerFromSmilesWAP(iMol);
            for (int j = i; j < dataSetSize; j++) {
                String jMol = smilesData.get(j).split(this.smilesSeparator)[0];
                IAtomContainer jmol = ChemUtility.getIAtomContainerFromSmilesWAP(jMol);
                double similarity = Double.valueOf(df.format(Tanimoto.calculate(mfp.getBitFingerprint(imol), mfp.getBitFingerprint(jmol))));
                diversityMatrix[i][j] = similarity;
                diversityMatrix[j][i] = similarity;
            }
        }
        System.out.println(GeneralUtility.twoDimArrayToString(diversityMatrix));
        return diversityMatrix;
    }

    public void getMaximumSimilarity(String pathToDataSet, boolean writeDataToFile) throws FileNotFoundException, IOException, CDKException {
        double[][] diversityMat = analyseWithInDataSet(pathToDataSet, writeDataToFile);
        int size = diversityMat[0].length;
        double[] compMaxSimilarity = new double[size];
        double[] compMinSimilarity = new double[size];



        for (int i = 0; i < size; i++) {
            double max = Double.MIN_VALUE;
                    double min = Double.MAX_VALUE;
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    double temp = diversityMat[i][j];
                    
                    if (temp > max) {
                        max = temp;
                        compMaxSimilarity[i] = temp;
                    }
                    if (temp < min) {
                        min = temp;
                        compMinSimilarity[i] = temp;
                    }
                }
            }
        }

        System.out.println(GeneralUtility.arrayToString(compMaxSimilarity));
        System.out.println("==================================");
        System.out.println(GeneralUtility.arrayToString(compMinSimilarity));
    }

    private double getMinimumSimilarity() {
        return 0.0;
    }

    private void analyseDataSets(String pathToDataSet1, String pathToDataSet2) {
    }
}
