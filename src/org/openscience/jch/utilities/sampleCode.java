/* 
 * Copyright (C) 2013 Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.jch.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *
 * @author chandu
 */
public class sampleCode {

    public static void main(String[] args) throws FileNotFoundException, CDKException, CloneNotSupportedException, IOException {
        List<IAtomContainer> completeSet = ChemUtility.readIAtomContainersFromCML("/Users/chandu/Desktop/1JCH/SyngentaUpdate2/finalNWChemData.cml");
        for (IAtomContainer molecule : completeSet) {
            if (!ChemUtility.isCharged(molecule)) {
                System.out.println(molecule.getID());
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
                ChemUtility.setAromaticityProperty(molecule);
                ChemUtility.isStrained(molecule);
                ChemUtility.isHetero(molecule);

                ChemUtility.calculateDescriptor(molecule);

                for (IAtom atm : molecule.atoms()) {
                    // System.out.println(atm.getID());
                    if (atm.getSymbol().equalsIgnoreCase("h")) {
                        IAtom carbonAtm = molecule.getConnectedAtomsList(atm).get(0);
                        if (carbonAtm.getSymbol().equalsIgnoreCase("c")) {
                            String dataToWrite = "";
//                            System.out.println(molecule.getID() + ":" + atm.getID());
//                            System.out.println("Aromatic: " + (carbonAtm.getProperty("aromatic").toString()));
//                            System.out.println("Strained: " + atm.getSymbol() + ":" + atm.getProperty("strained"));
//                            System.out.println("C Atom:");
//                            System.out.print((String) carbonAtm.getProperty("valency") + ",");
//                            System.out.print((String) carbonAtm.getProperty("numberOfValenceFreeElectrons") + ",");
//                            System.out.print((String) carbonAtm.getProperty("sumOFBondIndices") + ",");
//                            System.out.print((String) carbonAtm.getProperty("mullikenCharge"));
//                            System.out.println("");
//                            System.out.println("Carbon Atom: " + ChemUtility.getDescriptorVales(carbonAtm, molecule));
//                            System.out.println("");
//                            System.out.println("H Atom:");
//                            System.out.print((String) atm.getProperty("valency") + ",");
//                            System.out.print((String) atm.getProperty("numberOfValenceFreeElectrons") + ",");
//                            System.out.print((String) atm.getProperty("sumOFBondIndices") + ",");
//                            System.out.print((String) atm.getProperty("mullikenCharge"));
//                            System.out.println("1JCH_NWCHEM: " + JCHAppender.extract1JCH((String) atm.getProperty("JCH"), atm, molecule));
//                            System.out.println("");
//                            System.out.println("H Atom:" + ChemUtility.getDescriptorVales(atm, molecule));
//                            System.out.println("");
//                            System.out.println("Bondlength:");
//                            System.out.println(ChemUtility.getDistance(carbonAtm, atm));
//                            System.out.println("Exp1JCH : " + atm.getProperty("Exp1JCH"));
                            
                            double[] bangles = ChemUtility.calculateBondAngles(atm, molecule);
                            dataToWrite = GeneralUtility.conCat(dataToWrite, molecule.getID(), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, ChemUtility.getSMILES(molecule), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, atm.getID(), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (carbonAtm.getProperty("aromatic").toString()), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (atm.getProperty("strained").toString()), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) carbonAtm.getProperty("valency"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) carbonAtm.getProperty("numberOfValenceFreeElectrons"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) carbonAtm.getProperty("sumOFBondIndices"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) carbonAtm.getProperty("mullikenCharge"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, ChemUtility.getDescriptorVales(carbonAtm, molecule), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, carbonAtm.getHybridization().toString(), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("valency"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("numberOfValenceFreeElectrons"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("sumOFBondIndices"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("mullikenCharge"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, ChemUtility.getDescriptorVales(atm, molecule), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("NWCHEM_1JCH"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("avgNWChem1JCH"), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, String.valueOf(ChemUtility.getDistance(carbonAtm, atm)), ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, (String) atm.getProperty("Exp1JCH"), ",");
                            //System.out.println("Bond angles:");
                            String ba = "";

                            for (double angles : bangles) {
                                //System.out.print(angles + ",");
                                ba = ba + Double.toString(angles) + ",";
                            }

                            dataToWrite = GeneralUtility.conCat(dataToWrite, ba, ",");
                            dataToWrite = GeneralUtility.conCat(dataToWrite, "", "\n");

                            GeneralUtility.appendToFile(dataToWrite, "/Users/chandu/Desktop/dataFinal.csv");
                            System.out.println(dataToWrite);


                        }
                    }
                    // System.out.println(atm.getSymbol());
                }
                System.out.println("*****************************************");
            }
        }
    }
}
