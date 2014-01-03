/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity2222;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jch.utilities.IteratingMolTableReader;
import org.openscience.jch.utilities.IteratingMolTableReader.CustomObject;

/**
 *
 * @author chandu
 */
public class DataImporter {

    private Initialize in = null;
    private String filePath = "";

    public DataImporter(Initialize Obj, String structureFilePath) {
        /**
         * StructureImporter imports the molecules from the multi .sdf file and
         * appends the connection table to the STRUCTURE column and all the
         * properties present in the sdf file are appended as a separate columns
         *
         * SMILES can be generated while reading the structures and can be
         * appened to the smiles column by setting append smiles to trueF
         *
         * Data is commited to the sqlite db in batches of 10,0000
         * molecules/commit.
         */
        this.in = Obj;
        this.filePath = structureFilePath;
    }

    public void importData() throws FileNotFoundException, CDKException, SQLException {
        System.out.println("In data importer");
        File sdfFile = new File(this.filePath);
        IteratingMolTableReader reader = new IteratingMolTableReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance(), true);
        CustomObject tempMolObj = null;
        int j = 1;
        int ID = in.getRowCount("completeDataSet") + 1;
        List<CustomObject> dataArray = new ArrayList<CustomObject>();
        while (reader.hasNext()) {
            tempMolObj = reader.nextEntry();
            if (j < 10000) {
                dataArray.add(tempMolObj);
                j++;
            } else {
                j = 0;
                ID = in.insertInToTable(dataArray, ID);
                dataArray.clear();
            }
            System.out.println(j);
        }
        ID = in.insertInToTable(dataArray, ID);
        dataArray.clear();

    }
}