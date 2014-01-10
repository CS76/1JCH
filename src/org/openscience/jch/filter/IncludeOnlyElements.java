/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class IncludeOnlyElements extends StringParameter {
    String[] nonElement = {"0","1","2","3","4","5","6","7","8","9","-","%","(",")","*",".","/","@","[","]","+","H"};
    private String condition = ""; 
    private List<String> nonElementSmilesSymbols = Arrays.asList(nonElement);
    
    public IncludeOnlyElements(String values) {
        this.setName("IncludeOnlyElements");
        String[] tempStringHolder = values.split(" ");
        this.setStringList(Arrays.asList(tempStringHolder));
        this.setCategory(3);
        
    }

    @Override
    public boolean test(IAtomContainer mol) {
        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        elements.removeAll(this.nonElementSmilesSymbols);
        if (this.getStringList().containsAll(elements)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}