/*
 * This class assumes that the atom numbering in the NWCHEM output file 
 * and the atom number in the structure file are same and writes the 
 * mulliken charges to the corresponding atoms and saves the file.
 */
package org.openscience.jch.nwchem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.jch.utilities.ChemUtility;

/**
 * @author chandu
 */
public class Sample {

    public static void main(String[] args) throws FileNotFoundException, IOException, CDKException {
        List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML("C:\\Users\\CS76\\Desktop\\mol.cml");
        for (IAtomContainer mol : molList) {
            setPropMap(mol);
            break;
        }
    }

    public static void setPropMap(IAtomContainer molecule) {
        Map<String, Map<String, String>> propertiesMap = new HashMap<String, Map<String, String>>();
        for (IAtom atom : molecule.atoms()) {
            if (atom.getSymbol().equalsIgnoreCase("h")) {
                IAtom cAtom = molecule.getConnectedAtomsList(atom).get(0);
                if (cAtom.getSymbol().equalsIgnoreCase("c")) {
                    getStringPropertyMap(atom);
                    getStringPropertyMap(cAtom);
                }
            }
        }
    }

    public static Map<String, String> getStringPropertyMap(IAtom atom) {
        Map<Object, Object> mappedProp = atom.getProperties();
        for (Object p : mappedProp.keySet()) {
            System.out.println(p.getClass().toString());
        }
        return null;
    }
}
