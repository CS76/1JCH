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
public abstract class Parameter {

    private boolean directCompute;
    private String name;
    private String userSpec;
    private boolean CDKBased = false;
    private int category = 0;
    private int priority = 0;

    public void setUserSpec(String spec) {
        this.userSpec = spec;
    }

    public String getUserSpec() {
        return this.userSpec;
    }

    public void setName(String parameterName) {
        this.name = parameterName;
    }

    public String getName() {
        return this.name;
    }

    public void setDirectCompute(boolean directlyComputed) {
        this.directCompute = directlyComputed;
    }

    public boolean getDirectCompute() {
        return this.directCompute;
    }

    public void setCategory(int cat) {
        this.category = cat;
    }

    public int getCategory() {
        return this.category;
    }
    public void setPriority(int pri) {
        this.priority = pri;
    }

    public int getPriority() {
        return this.priority;
    }

    public abstract boolean test(IAtomContainer mol);

    public abstract boolean test(List<String> elements);
    
    public abstract boolean test(String smiles);
}
