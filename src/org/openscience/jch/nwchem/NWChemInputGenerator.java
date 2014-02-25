/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.nwchem;

import java.io.File;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author chandu
 */
public class NWChemInputGenerator {

    public static void generateNWChemInput(IAtomContainerSet mol, String Path) throws CDKException, IOException {
        String endData_A = "\n" + "basis\n" + "  * library \"TZVP (DFT Orbital)\"\n" + "end\n" + "\n" + "dft\n" + "  odft\n" + "  xc b3lyp\n" + "end\n" + "\n" + "driver \n" + " maxiter 90\n" + "end\n" + "\n" + "task dft optimize\n" + "\n" + "property\n" +"    spinspin\n"+ "    mulliken\n" + "end\n" + "\n" + "task dft property";
        // String endData_B = "\n# beckehandh functional\n\ndft\n odft\n xc beckehandh\nend\n\ndriver\n maxiter 90\nend\n\n# property calculation is indirect spin-spin coupling\nproperty\n mulliken \nend\n\n# now set options for basis, optimisation and basis set calculation\n\nbasis\n  * library aug-cc-pvtz\n end\n\ntask dft optimize\n\nbasis\n  * library aug-cc-pvtz-j\nend\n\ntask dft property\n";
        String dataToWrite = "";
        String fileName = "";
        int i = 0;
        Iterable<IAtomContainer> completeDataSet = mol.atomContainers();
        for (IAtomContainer a : completeDataSet) {
            i++;
            fileName = a.getID();
            System.out.println(fileName);
            if (fileName == null) {
                fileName = String.valueOf(i);
            }

            dataToWrite = "";
            dataToWrite = dataToWrite + "start " + a.getID() + "_" + a.getAtomCount() + "\n\ntitle \"" + a.getID() + "_Optimisation and J-calculation\"\n\nmemory 10240 mb\n\ncharge 0\n\ngeometry units angstroms\n";
            for (IAtom atm : a.atoms()) {
                dataToWrite = dataToWrite + "   " + atm.getSymbol() + "       " + GeneralUtility.format_Double(atm.getPoint3d().x) + "     " + GeneralUtility.format_Double(atm.getPoint3d().y) + "     " + GeneralUtility.format_Double(atm.getPoint3d().z) + "\n";
            }
            dataToWrite = dataToWrite + "end\n" + endData_A;
            //System.out.println(dataToWrite);
            new File(Path+fileName+"/").mkdir();
            GeneralUtility.writeToTxtFile(dataToWrite, Path+fileName+"\\"+ fileName + ".nw");
        }
    }
}
