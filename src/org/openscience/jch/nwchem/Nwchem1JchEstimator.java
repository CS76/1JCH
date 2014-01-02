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
        List<IAtomContainer> molecules = ChemUtility.readIAtomContainersFromCML("/Users/chandu/Desktop/1JCH/SyngentaUpdate2/finalNWChemCompleteData3.cml");

        System.out.println(molecules.size());
        for (IAtomContainer mol : molecules) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            //System.out.println(mol.getID());
            if (!mol.getID().equalsIgnoreCase("63_NWChem_1JCH_mulliken")) {
                Map<Integer, List<IAtom>> map = ChemUtility.getEquivalentAtoms(mol);
                for (int i : map.keySet()) {
                    if (map.get(i).get(0).getSymbol().equalsIgnoreCase("h")) {
                        System.out.println(map.get(i).get(0).getSymbol());
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
