package org.openscience.jch.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

public class CDKDescriptorCalculator {

    public static void main(String[] args) throws CloneNotSupportedException, FileNotFoundException, CDKException {
        String sourceDirectory = "C:\\Users\\CS76\\Desktop\\dataExtracted\\1jch\\final\\";
        IAtomContainerSet molSet = new AtomContainerSet();
        File folder = new File(sourceDirectory);
        File[] listOfFiles = folder.listFiles();
        int count = 0;
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            if (!fileName.endsWith(".DS_Store")) {
                String pathToCmlFile = sourceDirectory + fileName + "\\" + fileName + "_NWChem_1JCH_mulliken.cml";
                System.out.println(pathToCmlFile);
                IAtomContainer molecule = ChemUtility.readIAtomContainerFromCML(pathToCmlFile);
                molSet.addAtomContainer(molecule);
                molecule = appendProperties(molecule);
                String pathToCmlPropFile = sourceDirectory + fileName + "\\" + fileName + "_NWChem_1JCH_mulliken_CDK.cml";
                ChemUtility.writeToCmlFile(molecule, pathToCmlPropFile);
            }
        }
    }

    public static void deleteFile() {
        String sourceDirectory = "C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\";
        IAtomContainerSet molSet = new AtomContainerSet();
        File folder = new File(sourceDirectory);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            String fileToDelete = sourceDirectory + fileName + "\\" + fileName + "1_SP_NWChem_1JCH_mulliken_CDK.cml";
            System.out.println(fileToDelete);
            try {
                File file = new File(fileToDelete);
                if (file.delete()) {
                    System.out.println(file.getName() + " is deleted!");
                } else {
                    System.out.println("Delete operation is failed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void mergeAllCML() throws FileNotFoundException, CDKException {
        String sourceDirectory = "C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\";
        IAtomContainerSet molSet = new AtomContainerSet();
        File folder = new File(sourceDirectory);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            String pathToCmlFile = sourceDirectory + fileName;
            List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML(pathToCmlFile);
            System.out.println(molList.size());
            for (IAtomContainer molecule : molList) {
                molSet.addAtomContainer(molecule);
            }
        }
        System.out.println(molSet.getAtomContainerCount());
        ChemUtility.writeToCmlFile(molSet, sourceDirectory + "\\all_NWChem_1JCH_mulliken_CDK.cml");
    }

    public static IAtomContainer appendProperties(IAtomContainer molecule) throws CloneNotSupportedException {
        IAtomContainer mol = molecule;
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            ChemUtility.calculateDescriptor(mol);
            ChemUtility.isStrained(mol);
            ChemUtility.isAttachedToHetero(mol);
            ChemUtility.setAromaticityProperty(mol);
            for (IAtom atm : mol.atoms()) {
                System.out.println(atm.getSymbol());
                if (atm.getSymbol().equalsIgnoreCase("h")) {
                    IAtom carbonAtm = molecule.getConnectedAtomsList(atm).get(0);
                    if (carbonAtm.getSymbol().equalsIgnoreCase("c")) {
                        double angles[] = ChemUtility.calculateBondAngles(atm, mol);
                        atm.setProperty("angles", GeneralUtility.arrayToString(angles, ":"));
                    }
                }
                try {
                    atm.setProperty("hybridization", atm.getHybridization().toString());
                } catch (Exception e) {
                    atm.setProperty("hybridization", "unknown");
                }
                System.out.println(atm.getProperties());
            }
            mol = ChemUtility.setDescriptorValuesAsProperties(mol);
        } catch (CDKException ex) {
            Logger.getLogger(CDKDescriptorCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mol;
    }
}