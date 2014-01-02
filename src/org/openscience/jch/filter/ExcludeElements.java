/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.Arrays;
import java.util.List;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class ExcludeElements extends StringParameter {

    public ExcludeElements(String values) {
        this.setName("IncludeOnlyElements");
        this.setCategory(1);
        this.setStringList(Arrays.asList(values.split(" ")));
    }

    @Override
    public boolean test(IAtomContainer mol) {
        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        for (String s : elements) {
            if (this.getStringList().contains(s.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(String smiles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}