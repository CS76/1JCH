/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.jch.utilities.ChemUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {
    public static void main(String[] args) throws FileNotFoundException, CDKException, IOException {
        IAtomContainerSet molecules = ChemUtility.readIAtomContainersFromSDF("/Users/chandu/Desktop/diversity/SP/SP_diverseSubSet_maxmin.sdf");
        System.out.println(molecules.getAtomContainerCount());
        int id = 1;
        for(IAtomContainer mol: molecules.atomContainers()){
            mol.setID(id+"_SP");
            System.out.println(mol.getID());
            id++;
        }
        NWChemInputGenerator.generateNWChemInput(molecules, "/Users/chandu/Desktop/diversity/SP/NWChemInput/");
    }
}
