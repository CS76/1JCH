/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.CDKHydrogenAdder;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class SP2Hydrogens extends CDKBased {

    private boolean status;
    private Hybridization hybridization;

    public SP2Hydrogens(String values) {
        this.setName("SPHydrogens");
        this.setCategory(1);
        this.status = Boolean.valueOf(values);
        this.hybridization = IAtomType.Hybridization.SP2;
        this.setPriority(2);

    }

    @Override
    public boolean test(IAtomContainer mol) {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        try {
            CDKHydrogenAdder.getInstance(builder).addImplicitHydrogens(mol);
            for (IAtom atm : mol.atoms()) {
                if (atm.getSymbol().equalsIgnoreCase("c") && atm.getHybridization().equals(this.hybridization)) {
                    if (atm.getImplicitHydrogenCount() != 0) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SP2Hydrogens.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        return false;
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}