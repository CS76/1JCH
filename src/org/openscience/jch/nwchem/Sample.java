/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.jch.utilities.ChemUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {

    public static void main(String[] args) throws FileNotFoundException, CDKException, IOException {
        IAtomContainerSet molecules = ChemUtility.readIAtomContainersFromSDF("C:\\Users\\CS76\\Desktop\\168.sdf");
        System.out.println(molecules.getAtomContainerCount());
        WeightDescriptor wd = new WeightDescriptor();
        int id = 1;
        IAtomContainerSet moleculesNew = new AtomContainerSet();
        for (IAtomContainer mol : molecules.atomContainers()) {
            double molWt = Double.valueOf(wd.calculate(mol).getValue().toString());
            if (molWt < 1000) {
                mol.setID(id + "_napthaldehyde");
                System.out.println(mol.getID());
                moleculesNew.addAtomContainer(mol);
            }
            else{
                System.out.println(molWt);
            }
            id++;
        }
        NWChemInputGenerator.generateNWChemInput(moleculesNew, "C:\\Users\\CS76\\Desktop\\");
    }
}
