/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.smiles.SmilesGenerator;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class SPHydrogens extends CDKBased {

    private boolean status;
    private Hybridization hybridization;
    public SPHydrogens(String values) {
        this.setName("SPHydrogens");
        this.setCategory(2);
        this.status = Boolean.valueOf(values);
        this.hybridization = IAtomType.Hybridization.SP1;

    }

    @Override
    public boolean test(IAtomContainer mol) {
        SmilesGenerator sg = new SmilesGenerator();
        for (IAtom atm : mol.atoms()) {
            if (atm.getHybridization().equals(this.hybridization)) {
                if (atm.getImplicitHydrogenCount() != 0) {
                    return true;
                }
            }
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