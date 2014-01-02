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
public class IncludeNegativelyCharged extends BooleanParameter {
    
    public IncludeNegativelyCharged(){
        this.setCategory(1);
    }
    
    @Override
    public boolean test(IAtomContainer mol) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean test(List<String> elements) {
        if (elements.contains("-")) {
            if (this.getStatus()){
                return true;
            }else{
                return false;
            }
            
        } else{
            if (this.getStatus()){
                return false;
            }else{
                return true;
            }
        }
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
