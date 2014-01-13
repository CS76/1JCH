/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.ChemUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Debugger {

    public static void main(String[] args) throws FileNotFoundException, IOException, CDKException {

        BufferedReader br = new BufferedReader(new FileReader("/Users/chandu/Desktop/diversity/Passed.smi"));
        try {
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                try {
                    //System.out.println(line);
                    IAtomContainer mol = ChemUtility.getIAtomContainerFromSmilesWAPHA(line.split(" ")[0]);
                } catch (Exception e) {
                    System.out.println("line: "+line + "Error ==========================>");
                }
            }
        } finally {
            br.close();
        }
    }
}
