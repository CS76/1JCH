/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.utilities;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class JsonGenerator {

    public static void main(String[] args) {
        try {
            List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML("C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\all_NWChem_1JCH_mulliken_CDK_Final.cml");
            StringBuilder sb = new StringBuilder();
            sb.append("{\"molecules\" : [");
            for (IAtomContainer mol : molList) {
                sb.append("{\"MolID\" : \"" + mol.getID() + "\",");
                sb.append("\"AtomsList\" : [");
                System.out.println(mol.getID());
                for (IAtom atom : mol.atoms()) {
                    if (atom.getSymbol().equalsIgnoreCase("h")) {
                        if (mol.getConnectedAtomsList(atom).get(0).getSymbol().equalsIgnoreCase("C")) {
                            sb.append("{\"AtomID\" : \"" + atom.getID() + "\",");
                            sb.append("\"1JCH\" : \"" + atom.getProperty("1JCH") + "\",");
                            sb.append("\"JCH\" : \"" + atom.getProperty("JCH") + "\"");
                            System.out.println("\"" + atom.getID() + "\":\"" + atom.getProperty("1JCH") + "\",");
                            sb.append("},");
                        }
                    }
                }
                sb.append("]},");               
            }
             sb.append("]},");
             System.out.println(sb.toString().replace(",]","]"));
             GeneralUtility.writeToTxtFile(sb.toString().replace(",]","]"), "C:\\Users\\CS76\\Desktop\\new.json");
        } catch (IOException ex) {
            Logger.getLogger(JsonGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CDKException ex) {
            Logger.getLogger(JsonGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
