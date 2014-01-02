/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        ParameterEngine pe = new ParameterEngine("/Users/chandu/Desktop/filter", "/Users/chandu/Desktop/filter/parameter.txt", "/Users/chandu/Desktop/filter/SP.txt");
        //System.out.println(GeneralUtility.readLines("/Users/chandu/Desktop/filter/eMolecules/version.smi").size());
        BufferedReader br = new BufferedReader(new FileReader("/Users/chandu/Desktop/filter/SP_screened.txt"));
        int count = 0;
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                count += 1;
                line = br.readLine();
            }
        } finally {
            System.out.println(count);
            br.close();
        }
    }
}
