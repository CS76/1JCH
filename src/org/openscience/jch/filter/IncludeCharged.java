/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class IncludeCharged extends BooleanParameter {
    
    public IncludeCharged(String values){
        this.setName("IncludeCharged");
        this.setCategory(1);
        this.setStatus( Boolean.valueOf(values));
    }
    
    @Override
    public boolean test(IAtomContainer mol) {
        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        boolean charged;
        if (elements.contains("+") || elements.contains("-")) {
            charged = true;
        } else {
            charged = false;
        }

        if (this.getStatus() == charged) {
            return true;    
        } else {
            return false;
        }
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
