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
public class IncludeSMARTS extends StringParameter {

    public IncludeSMARTS(String values) {
        this.setName("IncludeOnlyElements");
        this.setStringList(Arrays.asList(values.split(" ")));
        this.setCategory(3);
    }

    @Override
    public boolean test(IAtomContainer mol) {
        return false;
    }

    @Override
    public boolean test(List<String> elements) {
        return false;
    }

    public boolean test(String smiles) {
        for (String s : this.getStringList()) {
            if (smiles.contains(s)) {
//                if (s.equalsIgnoreCase("C#C")) {
//                    int index = smiles.indexOf(s);
//                    if (index == 0 || index == smiles.length() - 3) {
//                        return true;
//                    }
//                } else if (s.equalsIgnoreCase("C#C)")) {
//                    return true;
//                }
                return true;
            }
        }
        return false;
    }
}
