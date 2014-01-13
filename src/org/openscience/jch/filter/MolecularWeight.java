/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class MolecularWeight extends CDKBased {

    public MolecularWeight(String values) {
        this.setName("MolecularWeight");
        this.setCategory(1);
        String[] inputvalues = values.split(" ");
        this.setMin(Integer.valueOf(inputvalues[0]));
        this.setMax(Integer.valueOf(inputvalues[1]));
        this.setPriority(2);
    }

    @Override
    public boolean test(IAtomContainer mol) {
        WeightDescriptor wd = new WeightDescriptor();
        double molWt = Double.valueOf(wd.calculate(mol).getValue().toString());
        if (molWt > this.getMin() && molWt < this.getMax()) {
            return true;
        } else {
            return false;
        }
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