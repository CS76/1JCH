/*
 * This class assumes that the atom numbering in the NWCHEM output file 
 * and the atom number in the structure file are same and writes the 
 * mulliken charges to the corresponding atoms and saves the file.
 */
package org.openscience.jch.nwchem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;

/**
 * @author chandu
 */
public class MullikenChargeAppender {

    private static List<atomObject> listAtoms = new ArrayList<atomObject>();

    public static void main(String[] args) throws FileNotFoundException, IOException, CDKException {
        // readOutPut("");
        File folder = new File("C:\\Users\\CS76\\Desktop\\SP2_1_extracted\\");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File subFolder = listOfFiles[i];
            if (!subFolder.getName().equalsIgnoreCase(".DS_Store")) {
                System.out.println(subFolder);
                readOutPut(subFolder);
            }
        }
    }

    public static void readOutPut(File Path) throws IOException, FileNotFoundException, CDKException {

        System.out.println(Path + "\\mullikenOutput.txt");
        List<String> lines = GeneralUtility.readLines(Path + "\\mullikenOutput.txt");
        List<String> wantedData = new ArrayList<String>();
        boolean read = false;
        for (int cl = 0; cl < lines.size(); cl++) {
            String line = lines.get(cl);
            if (line.contains(" Alpha Spin S,P,D,... shell population")) {
                for (int al = cl + 4; al < lines.size(); al++) {
                    if (lines.get(al).replace(" ", "").length() == 0) {
                        cl = al;
                        line = lines.get(cl);
                        break;
                    }
                    List<String> tempList = GeneralUtility.getNonEmptyArrayList(lines.get(al).split(" "));
                    atomObject tempAtom = new atomObject(tempList.get(0), tempList.get(1));
                    tempAtom.setAlphaSpin_S(tempList.get(2));
                    tempAtom.setAlphaSpin_D(tempList.get(3));
                    tempAtom.setAlphaSpin_P(tempList.get(4));
                    listAtoms.add(tempAtom);
                }
            }

            if (line.contains(" Beta Spin  S,P,D,... shell population")) {
                for (int bl = cl + 4; bl < lines.size(); bl++) {
                    if (lines.get(bl).replace(" ", "").length() == 0) {
                        cl = bl;
                        line = lines.get(cl);
                        break;
                    }
                    List<String> tempList = GeneralUtility.getNonEmptyArrayList(lines.get(bl).split(" "));
                    atomObject tempObj = listAtoms.get(Integer.valueOf(tempList.get(0)) - 1);
                    tempObj.setBetaSpin_S(tempList.get(2));
                    tempObj.setBetaSpin_P(tempList.get(3));
                    tempObj.setBetaSpin_D(tempList.get(4));
                }
            }
            if (line.contains("Total      S,P,D,... shell population")) {
                for (int tl = cl + 4; tl < lines.size(); tl++) {
                    if (lines.get(tl).replace(" ", "").length() == 0) {
                        cl = tl;
                        break;
                    }
                    List<String> tempList = GeneralUtility.getNonEmptyArrayList(lines.get(tl).split(" "));
                    atomObject tempObj = listAtoms.get(Integer.valueOf(tempList.get(0)) - 1);
                    System.out.println(listAtoms.size());
                    tempObj.setTSP_S(tempList.get(2));
                    tempObj.setTSP_P(tempList.get(3));
                    tempObj.setTSP_D(tempList.get(4));
                }
            }

            if (line.contains("Large bond indices")) {
                for (int lbl = cl + 2; lbl < lines.size(); lbl++) {
                    if (lines.get(lbl).replace(" ", "").length() == 0) {
                        cl = lbl;
                        break;
                    }
                    List<String> tempList = GeneralUtility.getNonEmptyArrayList(lines.get(lbl).split(" "));

                    if (tempList.get(1).equalsIgnoreCase("H")) {
                        atomObject tempObj = listAtoms.get(Integer.valueOf(tempList.get(0)) - 1);
                        tempObj.setLargeBondIndices(tempList.get(5));
                    } else if (tempList.get(4).equalsIgnoreCase("H")) {
                        atomObject tempObj = listAtoms.get(Integer.valueOf(tempList.get(3)) - 1);
                        tempObj.setLargeBondIndices(tempList.get(5));
                    }

                }
            }

            if (lines.get(cl).length() == 97) {
                List<String> tempList = GeneralUtility.getNonEmptyArrayList(lines.get(cl).split(" "));
                atomObject tempObj = listAtoms.get(Integer.valueOf(tempList.get(0)) - 1);
                tempObj.setValency(tempList.get(2));
                tempObj.setSumOfBondIndices(tempList.get(3));
                tempObj.setNumberOfValenceFreeElectrons(tempList.get(4));
                tempObj.setMullikenCharge(tempList.get(5));
            }

        }

        System.out.println(Path + "/" + Path.getName() + "_NWChem_1JCH.cml");
        IAtomContainer molecule = ChemUtility.readIAtomContainerFromCML(Path + "\\" + Path.getName() + "_NWChem_1JCH.cml");
        int index = 0;
        for (IAtom abc : molecule.atoms()) {
            atomObject ab = listAtoms.get(index);
            if (abc.getSymbol().equalsIgnoreCase(ab.atomSymbol)) {
                abc.setProperty("valency", ab.valency);
                abc.setProperty("numberOfValenceFreeElectrons", ab.numberOfValenceFreeElectrons);
                abc.setProperty("sumOFBondIndices", ab.sumOfBondIndices);
                abc.setProperty("mullikenCharge", ab.mullikenCharge);
                abc.setProperty("AlphaSpin_S", ab.alphaSpin_S);
                abc.setProperty("AlphaSpin_P", ab.alphaSpin_P);
                abc.setProperty("AlphaSpin_D", ab.alphaSpin_D);
                abc.setProperty("BetaSpin_S", ab.betaSpin_S);
                abc.setProperty("BetaSpin_P", ab.betaSpin_P);
                abc.setProperty("BetaSpin_D", ab.betaSpin_D);
                abc.setProperty("TotalSpinPopulation_S", ab.TSP_S);
                abc.setProperty("TotalSpinPopulation_P", ab.TSP_P);
                abc.setProperty("TotalSpinPopulation_D", ab.TSP_D);
                abc.setProperty("LargeBondIndices", ab.LargeBondIndices);
            }
            index += 1;
        }

        ChemUtility.writeToCmlFile(molecule, Path + "\\" + Path.getName() + "_NWChem_1JCH_mulliken.cml");
        lines.clear();
        wantedData.clear();
        listAtoms.clear();
    }

    public static class atomObject {

        private int atomNumber;
        private String atomSymbol;
        private double valency;
        private double numberOfValenceFreeElectrons;
        private double sumOfBondIndices;
        private double mullikenCharge;
        private double alphaSpin_S;
        private double alphaSpin_P;
        private double alphaSpin_D;
        private double betaSpin_S;
        private double betaSpin_P;
        private double betaSpin_D;
        private double TSP_S;
        private double TSP_P;
        private double TSP_D;
        private double LargeBondIndices = 0.0;

        public atomObject() {
        }

        public atomObject(String i, String symbol) {
            this.atomNumber = Integer.valueOf(i);
            this.atomSymbol = symbol;
        }

        public void setAlphaSpin_S(String ass) {
            this.alphaSpin_S = Double.valueOf(ass);
        }

        public void setAlphaSpin_P(String asp) {
            this.alphaSpin_P = Double.valueOf(asp);
        }

        public void setAlphaSpin_D(String asd) {
            this.alphaSpin_D = Double.valueOf(asd);
        }

        public void setBetaSpin_S(String ass) {
            this.betaSpin_S = Double.valueOf(ass);
        }

        public void setBetaSpin_P(String asp) {
            this.betaSpin_P = Double.valueOf(asp);
        }

        public void setBetaSpin_D(String asd) {
            this.betaSpin_D = Double.valueOf(asd);
        }

        public void setValency(String v) {
            this.valency = Double.valueOf(v);
        }

        public void setNumberOfValenceFreeElectrons(String vfe) {
            this.numberOfValenceFreeElectrons = Double.valueOf(vfe);
        }

        public void setSumOfBondIndices(String sbi) {
            this.sumOfBondIndices = Double.valueOf(sbi);
        }

        public void setMullikenCharge(String mc) {
            this.mullikenCharge = Double.valueOf(mc);
        }

        public void setTSP_S(String tsps) {
            this.TSP_S = Double.valueOf(tsps);
        }

        public void setTSP_P(String tspp) {
            this.TSP_P = Double.valueOf(tspp);
        }

        public void setTSP_D(String tspd) {
            this.TSP_D = Double.valueOf(tspd);
        }

        public void setLargeBondIndices(String lbi) {
            this.LargeBondIndices = Double.valueOf(lbi);
        }
    }
}
