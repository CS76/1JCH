/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.ChemUtility;

/**
 *
 * @author chandu
 */
public class JCHAppender {

    public static void main(String[] args) throws FileNotFoundException, CDKException, IOException {
        File folder = new File("/Users/chandu/Desktop/sp_COSMOS/");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            //          System.out.println(fileName);
            if (!fileName.endsWith(".DS_Store")) {
                String pathToCmlFile = "/Users/chandu/Desktop/sp_COSMOS/" + fileName + "/" + fileName + "_NWChem_coord.cml";
                String pathToExtractedJCHFile = "/Users/chandu/Desktop/sp_COSMOS/" + fileName + "/extractedJCH.txt";
                IAtomContainer molecule = ChemUtility.readIAtomContainerFromCML(pathToCmlFile);
                ChemUtility.writeToCmlFile(appendJCH(molecule, extractJCH(pathToExtractedJCHFile)), "/Users/chandu/Desktop/sp_COSMOS/" + fileName + "/" + fileName + "_NWChem_1JCH.cml");
            }
        }
    }

    public static IAtomContainer appendJCH(IAtomContainer mol, Map mp) throws CDKException, IOException {
        for (IAtom atm : mol.atoms()) {
            if (atm.getSymbol().equalsIgnoreCase("h")) {
                System.out.println(mp.get(atm.getID()));
                atm.setProperty("JCH", mp.get(atm.getID()));
            }
        }
        return mol;
    }

    public static Map extractJCH(String path) throws FileNotFoundException, IOException {
        Map<String, List<String>> mappedJCH = new HashMap<String, List<String>>();
        FileReader inputFile = new FileReader(path);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        String line;
        while ((line = bufferReader.readLine()) != null) {
            if (line.length() > 3) {
                if (line.subSequence(0, 4).equals("Atom")) {
                    String[] data = line.split("   ");
                    if (data[0].contains("(1-H)")) {
                        if (data[1].contains("(13-C)")) {
                            int hydrogenNum = Integer.valueOf(data[0].split(":")[1].split(" ")[0]);
                            List<String> JCH = mappedJCH.get("a" + hydrogenNum);
                            if (JCH == null) {
                                JCH = new ArrayList<String>();
                                mappedJCH.put("a" + hydrogenNum, JCH);
                            }
                            JCH.add("a" + data[1].split(":")[1].split(" ")[0] + ";" + data[2].split(":")[1]);
                        }
                    }
                }
            }
        }
        return mappedJCH;
    }

    public static String extract1JCH(String JCHproperty, IAtom hAtom, IAtomContainer molecule) {
        String JCH1 = "";
        String[] JCHValues = (hAtom.getID() + ":" +JCHproperty).replace("[", "").replace("]", "").split(":")[1].split(",");
        System.out.println(JCHValues.length);
        for (String JCH_1 : JCHValues) {
            String cAtmID = JCH_1.split(";")[0];
            IAtom cAtm = molecule.getConnectedAtomsList(hAtom).get(0);
            if (cAtmID.replace(" ", "").equalsIgnoreCase(cAtm.getID())) {
                JCH1 =  JCH_1.split(";")[1].replace("Hz", "").replace(" ", "");
            }
        }
        return JCH1;
    }
    
    
}
