/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.util.Comparator;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class ParameterComparator implements Comparator<Parameter> {
    @Override
    public int compare(Parameter p1, Parameter p2) {
        return p1.getPriority()-p2.getPriority();
    }
}