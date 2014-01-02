/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openscience.cdk.exception.CDKException;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class JsonGenerator {

    public static void main(String[] args) throws FileNotFoundException, IOException, CDKException {
        List<String> lines = GeneralUtility.readLines("/Users/chandu/Desktop/COMPLETE.csv");
        List<JsonObject> jsonObjList = new ArrayList<JsonObject>();
        for (int i = 1; i < lines.size(); i++) {
           // System.out.println(lines.get(i));
            String[] values = lines.get(i).split(",");
            //System.out.println(values.length);
            JsonObject presentMol = null;
            boolean present = false;
            for (JsonObject jo : jsonObjList) {
                if (jo.MolID.equalsIgnoreCase(values[0])) {
                    present = true;
                    presentMol = jo;
                }
            }

            if (present) {
                presentMol.listSubObj.add(new SubObject(values[2], values[3], values[5], values[4]));
            } else {
                jsonObjList.add(new JsonObject(values[0], values[1], values[2], values[3], values[5], values[4]));
            }

        }
        String totalData = "{\"molecules\" : [\n";
        String dataToWrite = "";
        JsonObject job= null;
        
        List<Integer> molList = new ArrayList<Integer>();
        for(JsonObject tempJob:jsonObjList){
            molList.add(tempJob.id);
        }
        Collections.sort(molList);
        
        System.out.println(molList);
        int count = 0 ;
        for (int i: molList) {
            count +=1;
            for(JsonObject tempJob:jsonObjList){
                //System.out.println(tempJob.id+"-");
                if (tempJob.id == i){
                   System.out.println("here"+","+(i));
                    job = tempJob;
                    break;
                }
            }
            
            dataToWrite = "{\"MolID\" : \"" + job.MolID + "\",\n";
            dataToWrite = dataToWrite + "\"SMILES\" : \"" + job.Smiles + "\",\n";
            dataToWrite = dataToWrite + "\"ID\" : \"" + String.valueOf(job.id) + "\",\n";
            dataToWrite = dataToWrite + "\"AtomsList\" :[\n";
            for (int j =0 ;j < job.listSubObj.size();j++) {
                SubObject sob= job.listSubObj.get(j);
                //System.out.println(sob.AtomID);
                dataToWrite = dataToWrite + "{";
                dataToWrite = dataToWrite + "\"ATOMID\" : \"" + sob.AtomID + "\",\n";
                dataToWrite = dataToWrite + "\"HYBRIDIZATION\" : \"" + sob.Hybridization + "\",\n";
                dataToWrite = dataToWrite + "\"EXP_1JCH\" : \"" + sob.EXP_1JCH + "\",\n";
                dataToWrite = dataToWrite + "\"NWCHEM_1JCH\" : \"" + sob.NWCHEM_1JCH + "\"";
                dataToWrite = dataToWrite + "}\n";
                if (j < job.listSubObj.size()-1) {
                    dataToWrite = dataToWrite + ",";
                }
            }
            dataToWrite = dataToWrite + "]";
           // System.out.println(i);
            if (count < molList.size()) {
                totalData = totalData  + dataToWrite + "},\n";
                } else {
                totalData = totalData + dataToWrite + "}\n" ;
            }
            
        }
        System.out.println(molList.size());
        totalData = totalData + "]\n}\n";
        System.out.println(totalData);
        GeneralUtility.writeToTxtFile(totalData, "/Users/chandu/Desktop/final.json");
    }

    public static class JsonObject {
        int id = 0;
        String MolID = "";
        String Smiles = "";
        List<SubObject> listSubObj = new ArrayList<SubObject>();

        public JsonObject(String molID, String smiles,String atomid, String hybrid, String exp1jch, String nwchem1jch) {
            this.MolID = molID;
            this.Smiles = smiles;
            this.id = Integer.parseInt(molID.replace("_NWChem_1JCH_mulliken", ""));
            listSubObj.add(new SubObject(atomid, hybrid, exp1jch, nwchem1jch));
        }
    }

    public static class SubObject {

        String Hybridization = "";
        String NWCHEM_1JCH = "";
        String AtomID = "";
        String EXP_1JCH = "";

        public SubObject(String atomid,String hybrid, String exp1jch, String nwchem1jch) {
            this.Hybridization = hybrid;
            this.EXP_1JCH = exp1jch;
            this.NWCHEM_1JCH = nwchem1jch;
            this.AtomID = atomid;
        }
    }
}
