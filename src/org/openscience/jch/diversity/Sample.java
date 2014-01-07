/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import org.openscience.cdk.exception.CDKException;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class Sample {
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException, CDKException {
        InitializeDatabase id = new InitializeDatabase("");
        id.populateStructureData("/Users/chandu/Desktop/filter/SP_screened.txt");
        id.generateMACCSKey();
        id.randomizeTable("completeDataSet");
        id.OptiSim();
        
    }
}
