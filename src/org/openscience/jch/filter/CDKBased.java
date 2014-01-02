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
public abstract class CDKBased extends Parameter {

    private double max = 0;
    private double min = 0;

    public void setMax(double maxValue) {
        this.max = maxValue;
    }

    public double getMax() {
        return this.max;
    }

    public void setMin(int minValue) {
        this.min = minValue;
    }

    public double getMin() {
        return this.min;
    }

    @Override
    public abstract boolean test(IAtomContainer mol);

    public abstract boolean test(List<String> elements);
}
