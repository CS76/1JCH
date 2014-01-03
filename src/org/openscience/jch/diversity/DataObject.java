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
    private String molTable = "";

    DataObject() {
    }

    DataObject(int molID, String connecTable, String Smiles, byte[] fp) {
        this.ID = molID;
        this.MACCSKEYS = fp;
        this.smiles = Smiles;
        this.molTable = connecTable;
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

    public String getMolTable() {
        return this.molTable;
    }

    public void setMolTable(String molT) {
        this.molTable = molT;
    }
}