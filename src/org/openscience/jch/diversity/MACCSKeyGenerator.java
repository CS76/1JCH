/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.diversity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.jch.utilities.ChemUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
class MACCSKeyGenerator extends RecursiveTask<Map< Integer, byte[]>> {

    private Map<Integer, String> smilesData;
    Map< Integer, byte[]> MACCSData = new HashMap< Integer, byte[]>();

    public MACCSKeyGenerator(Map<Integer, String> data) {
        this.smilesData = data;
    }

    @Override
    protected Map<Integer, byte[]> compute() {
        System.out.println("Started");
        MACCSFingerprinter mp = new MACCSFingerprinter();

        for (int i : this.smilesData.keySet()) {
            try {
                byte[] bi = mp.getBitFingerprint(ChemUtility.getIAtomContainerFromSmilesWAP(this.smilesData.get(i))).asBitSet().toByteArray();
                this.MACCSData.put(i, bi);
            } catch (CDKException ex) {
                Logger.getLogger(MACCSKeyGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.MACCSData;
    }
}