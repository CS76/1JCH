/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.jch.utilities.ChemUtility;
/**
 *
 * @author CS76
 */
public class OneBondJCHExtractor {

    public static void main(String[] args) {
        try {
            int count = 0;
            String filePath = "C:\\Users\\CS76\\Desktop\\new.cml";
            
            List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML(filePath);
            IAtomContainerSet finalMolSet = new AtomContainerSet();
            for (IAtomContainer molecule : molList) {
                for (IAtom atom: molecule.atoms()){
                    if (atom.getSymbol().equalsIgnoreCase("h")){
                        IAtom cAtom = molecule.getConnectedAtomsList(atom).get(0);
                        if(cAtom.getSymbol().equalsIgnoreCase("c")){
                            count++;
                           // System.out.print(count+"   ");
                            //System.out.println(JCHAppender.extract1JCH((String)atom.getProperty("JCH"),atom,molecule));
                            atom.setProperty("1JCH",JCHAppender.extract1JCH((String)atom.getProperty("JCH"),atom,molecule));
                        }
                    }
                }
                finalMolSet.addAtomContainer(molecule);
            }
            ChemUtility.writeToCmlFile(finalMolSet, "C:\\Users\\CS76\\Desktop\\finalNew.cml");
            System.out.println(count);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OneBondJCHExtractor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CDKException ex) {
            Logger.getLogger(OneBondJCHExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
