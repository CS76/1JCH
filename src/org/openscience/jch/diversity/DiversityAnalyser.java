/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IBitFingerprint;
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
    public static double[][] diversityMatrix;
    public static List<String> smilesData = new ArrayList<String>();

    public DiversityAnalyser() {
    }

    public DiversityAnalyser(String regexSplit) {
        this.smilesSeparator = regexSplit;
    }

    public double[][] analyseWithInDataSet(String pathToDataSet, boolean writeDataToFile) throws FileNotFoundException, IOException, CDKException {
        this.smilesData = GeneralUtility.readLines(pathToDataSet);
        DecimalFormat df = new DecimalFormat("#.####");
        final int dataSetSize = smilesData.size();


        this.diversityMatrix = new double[dataSetSize][dataSetSize];
        MACCSFingerprinter mfp = new MACCSFingerprinter();
        String[] header = new String[dataSetSize];
        int q = 0;
        for (String s : this.smilesData) {
            header[q] = s.split(" ")[1];
            q++;
        }
        System.out.println(GeneralUtility.arrayToString(header));
        final ForkJoinPool pool = new ForkJoinPool(4);
        final divCalculator finder = new divCalculator(0, dataSetSize);
        pool.invoke(finder);


//        for (int i = 0; i < dataSetSize; i++) {
//
//            String iMol = smilesData.get(i).split(this.smilesSeparator)[0];
//            header[i] = smilesData.get(i).split(this.smilesSeparator)[1];
//            IAtomContainer imol = ChemUtility.getIAtomContainerFromSmilesWAP(iMol);
//            for (int j = i; j < dataSetSize; j++) {
//                System.out.println(i + "," + j);
//                String jMol = smilesData.get(j).split(this.smilesSeparator)[0];
//                IAtomContainer jmol = ChemUtility.getIAtomContainerFromSmilesWAP(jMol);
//                double similarity = Double.valueOf(df.format(Tanimoto.calculate(mfp.getBitFingerprint(imol), mfp.getBitFingerprint(jmol))));
//                diversityMatrix[i][j] = similarity;
//                diversityMatrix[j][i] = similarity;
//            }
//        }
        //System.out.println(GeneralUtility.twoDimArrayToStringWithHeader(diversityMatrix,header));
        //System.out.println(Arrays.deepToString(diversityMatrix));
        System.out.println("contin");
        GeneralUtility.writeToTxtFile(GeneralUtility.twoDimArrayToStringWithHeader(diversityMatrix, header), "/Users/chandu/Desktop/correlation-explorer-gh-pages/examples/diversity.csv");
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

    class divCalculator extends RecursiveTask<String> {

        private int startID = 0;
        private int stopID = 0;

        public divCalculator(int start, int stop) {
            System.out.println("new task");
            this.startID = start;
            this.stopID = stop;
            System.out.println(this.startID + " | " + this.stopID);
        }

        @Override
        protected String compute() {
            final int length = this.stopID - this.startID;
            if (length < 10) {
                try {
                    return computeDirectly(this.startID, this.stopID);
                } catch (CDKException ex) {
                    Logger.getLogger(DiversityAnalyser.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            final int split = length / 2;
            final divCalculator left = new divCalculator(this.startID, this.startID + split);
            final divCalculator right = new divCalculator(this.startID + split, this.stopID);
            left.fork();
            right.compute();
            left.join();
            return null;
        }

        private String computeDirectly(int i, int j) throws CDKException {
            for (int st = i; st < j; st++) {
                String mol1 = DiversityAnalyser.smilesData.get(st).split(" ")[0];
                MACCSFingerprinter mfp = new MACCSFingerprinter();
                DecimalFormat df = new DecimalFormat("#.####");
                IAtomContainer molecule1 = ChemUtility.getIAtomContainerFromSmilesWAP(mol1);
                IBitFingerprint m1fp = mfp.getBitFingerprint(molecule1);
                for (int count = st; count < DiversityAnalyser.smilesData.size(); count++) {
                    if (st != count) {
                        try {
                            String mol2 = DiversityAnalyser.smilesData.get(count).split(" ")[0];
                            IAtomContainer molecule2 = ChemUtility.getIAtomContainerFromSmilesWAP(mol2);                            
                            double divv = Double.valueOf(df.format(Tanimoto.calculate(m1fp, mfp.getBitFingerprint(molecule2))));
                            DiversityAnalyser.diversityMatrix[st][count] = divv;
                            DiversityAnalyser.diversityMatrix[count][st] = divv;
                        } catch (Exception e) {
                            System.out.println(DiversityAnalyser.smilesData.get(st));
                        }
                    } else {
                        DiversityAnalyser.diversityMatrix[st][count] = 1.0;
                    }
                }
            }
            System.out.println("Doneee");
            return null;
        }
    }
}
