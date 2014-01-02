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
        File folder = new File("/Users/chandu/Desktop/NWChem/CD_done/");
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
        System.out.println(Path + "/mullikenOutput.txt");
        List<String> lines = GeneralUtility.readLines(Path + "/mullikenOutput.txt");
        List<String> wantedData = new ArrayList<String>();
        boolean read = false;
        for (String line : lines) {
            if (line.length() == 97) {
                wantedData.clear();
                System.out.println(line);
                String[] templi = line.split("  ");
                for (String each : templi) {
                    if (each.length() > 0) {
                        wantedData.add(each);
                    }
                }
                generateObject(wantedData);
            }
        }
        //System.out.println(listAtoms.size());
        System.out.println(Path + "/" + Path.getName() + "_NWChem_1JCH.cml");
        IAtomContainer molecule = ChemUtility.readIAtomContainerFromCML(Path + "/" + Path.getName() + "_NWChem_1JCH.cml");
        int index = 0;
        for (IAtom abc : molecule.atoms()) {
            //System.out.println(index);
            atomObject ab = listAtoms.get(index);
            //System.out.println(abc.getSymbol() + ":" + ab.atomSymbol);
            if (abc.getSymbol().equalsIgnoreCase(ab.atomSymbol)) {
                abc.setProperty("valency", ab.valency);
                abc.setProperty("numberOfValenceFreeElectrons", ab.numberOfValenceFreeElectrons);
                abc.setProperty("sumOFBondIndices", ab.sumOfBondIndices);
                abc.setProperty("mullikenCharge", ab.mullikenCharge);
            }
            index += 1;
        }
//        for (IAtom abca : molecule.atoms()) {
//            System.out.println(abca.getProperty("mullikenCharge"));
//        }
        ChemUtility.writeToCmlFile(molecule, Path + "/" + Path.getName() + "_NWChem_1JCH_mulliken.cml");
        lines.clear();
        wantedData.clear();
        listAtoms.clear();
    }

    public static void generateObject(List<String> atomData) {
        //System.out.println("-"+atomData.get(0)+"-");
        int id = Integer.valueOf(atomData.get(0).replace(" ", ""));
        String sym = atomData.get(1);
        double val = Double.valueOf(atomData.get(2));
        double nVE = Double.valueOf(atomData.get(3));
        double sOBI = Double.valueOf(atomData.get(4));
        atomObject tempAtom = new atomObject(id, sym, val, nVE, sOBI);
        listAtoms.add(tempAtom);
        //System.out.println(sym + "," + val + "," + nVE + "," + sOBI);
    }

    public static class atomObject {

        private int atomNumber;
        private String atomSymbol;
        private double valency;
        private double numberOfValenceFreeElectrons;
        private double sumOfBondIndices;
        private double mullikenCharge;

        public atomObject() {
        }

        public atomObject(int i, String symbol, double valency, double numberOfValenceFreeElectrons, double sumOfBondIndices) {
            System.out.println(i+","+symbol+","+valency+","+numberOfValenceFreeElectrons+","+sumOfBondIndices+": Object");
            this.atomNumber = i;
            this.atomSymbol = symbol;
            this.valency = valency;
            this.numberOfValenceFreeElectrons = numberOfValenceFreeElectrons;
            this.sumOfBondIndices = sumOfBondIndices;
            this.mullikenCharge = this.numberOfValenceFreeElectrons + this.sumOfBondIndices;
        }
    }
}