/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Nwchem1JchEstimator {

    public static void main(String[] args) throws FileNotFoundException, CDKException {

        //Reads all molecules in to a list
        List<IAtomContainer> molecules = ChemUtility.readIAtomContainersFromCML("/Users/chandu/Desktop/1JCH/SyngentaUpdate2/finalNWChemCompleteData3.cml");

        /**
         * For each IAtomContainer in the list(Except ethyne: CDK gives false
         * equivalent classes), perceive atom types and then loop through the
         * Map. 
         * 
         * For each class in the equivalent classes map, check if class is
         * of hydrogen atoms and if so add all the experimental and NWChem 1JCH
         * to separate arrays. if all the Exp-1JCH values in a equivalent class
         * are equal then find the average of 1JCH and add as prop. else
         * consider the NWchem 1JCH as the Avg-1JCH property
         * 
         * Finally writes the data to a multiMolecule-CML file
         */
        for (IAtomContainer mol : molecules) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            if (!mol.getID().equalsIgnoreCase("63_NWChem_1JCH_mulliken")) {
                Map<Integer, List<IAtom>> map = ChemUtility.getEquivalentAtoms(mol);
                for (int i : map.keySet()) {
                    if (map.get(i).get(0).getSymbol().equalsIgnoreCase("h")) {
                        String[] expPropArray = new String[map.get(i).size()];
                        String[] nwchemPropArray = new String[map.get(i).size()];
                        int j = 0;
                        for (IAtom atm : map.get(i)) {
                            String exp1JCH = atm.getProperty("Exp1JCH");
                            String nwchem1JCH = atm.getProperty("NWCHEM_1JCH");
                            if (exp1JCH != null) {
                                expPropArray[j] = exp1JCH;
                                nwchemPropArray[j] = nwchem1JCH;
                            } else {
                                expPropArray[j] = "";
                            }
                            j++;
                        }
                        if (GeneralUtility.isAllElementsEqual(expPropArray) && !expPropArray[0].equalsIgnoreCase("")) {
                            for (IAtom atm : map.get(i)) {
                                atm.setProperty("avgNWChem1JCH", GeneralUtility.getAverage(nwchemPropArray));
                            }
                        } else {
                            for (IAtom atm : map.get(i)) {
                                atm.setProperty("avgNWChem1JCH", atm.getProperty("NWCHEM_1JCH"));
                            }
                        }
                    }
                }
            }
        }

        IAtomContainerSet atmSet = new AtomContainerSet();
        for (IAtomContainer mol : molecules) {
            System.out.println(mol.getID());
            for (IAtom atom : mol.atoms()) {
                if (atom.getSymbol().equalsIgnoreCase("h")) {
                    if (atom.getProperty("avgNWChem1JCH") == null) {
                        atom.setProperty("avgNWChem1JCH", atom.getProperty("NWCHEM_1JCH"));
                    }
                }
            }
            atmSet.addAtomContainer(mol);
        }
        ChemUtility.writeToCmlFile(atmSet, "/Users/chandu/Desktop/finalNWChemData.cml");
    }
}
