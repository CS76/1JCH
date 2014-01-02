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
public abstract class BooleanParameter extends Parameter{
    private boolean status;
    
    public boolean getStatus(){
        return this.status;
    }
    
    public void setStatus(boolean state){
        this.status = state;
    }
    
    
    @Override
    public abstract boolean test(IAtomContainer mol);
    public abstract boolean test(List<String> elements);
    
}
