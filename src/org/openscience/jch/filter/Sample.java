/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, CDKException {
        final long startTime = System.currentTimeMillis();
        ParameterEngine pe = new ParameterEngine("/Users/chandu/Desktop/diversity", "/Users/chandu/Desktop/diversity/parameter.txt", "/Users/chandu/Desktop/diversity/Passed.smi");
        final long stopTime = System.currentTimeMillis();
        System.out.println("total exec time for complete eMolecules:" + (stopTime - startTime));
        //System.out.println(GeneralUtility.readLines("/Users/chandu/Desktop/filter/eMolecules/version.smi").size());
        System.out.println("PassedS:" + GeneralUtility.getRowCount("/Users/chandu/Desktop/diversity/SP2_SPPassed.smi"));
        System.out.println("FailedS:" + GeneralUtility.getRowCount("/Users/chandu/Desktop/diversity/SP2_SPFailed.smi"));
    }
}
