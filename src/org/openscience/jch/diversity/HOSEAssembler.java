/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.util.List;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 *
 * @author CS76
 */
public class HOSEAssembler {

    public static void main(String[] args) throws FileNotFoundException, CDKException {
        List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML("C:\\Users\\CS76\\Documents\\NetBeansProjects\\1JCH\\Data\\Experimental_NWChem_DataSet\\finalNWChemData.cml");
        System.out.println(molList.size());
        HOSECodeGenerator hg = new HOSECodeGenerator();
        for (IAtomContainer mol : molList) {
            for (IAtom atm : mol.atoms()) {
                if (atm.getSymbol().equalsIgnoreCase("c")) {
                    System.out.println(mol.getID()+","+hg.getHOSECode(mol, atm, 2)+"\"");
                }
            }
        }
    }
}
