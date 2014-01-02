/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public abstract class StringParameter extends Parameter{
    
    private List<String> stringList = new ArrayList<String>();
    
    
    public void setStringList(List<String> parameterValues){
        this.stringList = parameterValues;
    }
    
    public List<String> getStringList(){
        return this.stringList;
    }
    
    @Override
    public abstract boolean test(IAtomContainer mol);
    public abstract boolean test(List<String> elements);
    public abstract boolean test(String elements);
    
}