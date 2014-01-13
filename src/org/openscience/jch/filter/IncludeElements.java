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
public class IncludeElements extends StringParameter {

    public IncludeElements(String values) {
        this.setName("IncludeOnlyElements");
        List<String> temp = new ArrayList<String>();
        this.setStringList(Arrays.asList(values.split(" ")));
        this.setCategory(3);
        this.setPriority(1);
    }

    @Override
    public boolean test(IAtomContainer mol) {
        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        if (elements.containsAll(this.getStringList())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
