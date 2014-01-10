/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class DataObject {

    private int ID = 0;
    private IAtomContainer mol = null;
    private String smiles = "";
    private byte[] MACCSKEYS = null;
    private int customID1 = 0;
    private int customID2 = 0;
    private String UserData = "";

    DataObject() {
    }
    
     DataObject(int molID,String Smiles, byte[] fp) {
        this.ID = molID;
        this.MACCSKEYS = fp;
        this.smiles = Smiles;
    }

    DataObject(int molID, String Smiles, byte[] fp, String UserData) {
        this.ID = molID;
        this.MACCSKEYS = fp;
        this.smiles = Smiles;
        this.UserData = UserData;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int molID) {
        this.ID = molID;
    }

    public String getSmiles() {
        return this.smiles;
    }

    public void setSmiles(String inputSmiles) {
        this.smiles = inputSmiles;
    }

    public byte[] getFp() {
        return this.MACCSKEYS;
    }

    public void setFp(byte[] maccsKey) {
        this.MACCSKEYS = maccsKey;
    }

    public IAtomContainer getMolecule() {
        return this.mol;
    }

    public void setMolecule(IAtomContainer molecule) {
        this.mol = molecule;
    }

    public void setCustomID1(int id){
        this.customID1 = id;
    }
    
     public void setCustomID2(int id){
        this.customID2 = id;
    }
    
     public int getCustomID1(){
         return this.customID1;
     }
    
     
     public int getCustomID2(){
         return this.customID2;
     }
     
     public void setUserData(String data){
         this.UserData = data;
     }
     
     public String getUserData(){
         return this.UserData;
     }
}