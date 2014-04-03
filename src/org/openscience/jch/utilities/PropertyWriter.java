/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.utilities;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


/**
 *
 * @author CS76
 */
public class PropertyWriter {

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        int count =0;
        try {
            List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML("C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\all_NWChem_1JCH_mulliken_CDK_Final.cml");
            for (IAtomContainer molecule : molList) {
                ++count;
                for (IAtom atom : molecule.atoms()) {
                    if (atom.getSymbol().equalsIgnoreCase("h")) {
                        IAtom cAtom = molecule.getConnectedAtomsList(atom).get(0);
                        if (cAtom.getSymbol().equalsIgnoreCase("c")) {
                            sb.append("MolID_"+String.valueOf(count)).append(",");
                            sb.append(atom.getID()).append(",");
                            sb.append(extractHProperties(atom)).append(",");
                            sb.append(extractCProperties(cAtom)).append("\n");
                        }
                    }
                }
            }
            GeneralUtility.writeToTxtFile(sb.toString(),"C:\\Users\\CS76\\Desktop\\allData.csv");
        } catch (IOException ex) {
            Logger.getLogger(PropertyWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CDKException ex) {
            Logger.getLogger(PropertyWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String extractHProperties(IAtom hAtom) {
        String property = "";
//        JCH
        property += "," + hAtom.getProperty("JCH");
//        valency
        property += "," + hAtom.getProperty("valency");
//        numberOfValenceFreeElectrons
        property += "," + hAtom.getProperty("numberOfValenceFreeElectrons");
//        sumOFBondIndices
        property += "," + hAtom.getProperty("sumOFBondIndices");
//        mullikenCharge
        property += "," + hAtom.getProperty("mullikenCharge");
//        AlphaSpin_S
        property += "," + hAtom.getProperty("AlphaSpin_S");
//        AlphaSpin_P
        property += "," + hAtom.getProperty("AlphaSpin_P");
//        AlphaSpin_D
        property += "," + hAtom.getProperty("AlphaSpin_D");
//        BetaSpin_S
        property += "," + hAtom.getProperty("BetaSpin_S");
//        BetaSpin_P
        property += "," + hAtom.getProperty("BetaSpin_P");
//        BetaSpin_D
        property += "," + hAtom.getProperty("BetaSpin_D");
//        TotalSpinPopulation_S
        property += "," + hAtom.getProperty("TotalSpinPopulation_S");
//        TotalSpinPopulation_P
        property += "," + hAtom.getProperty("TotalSpinPopulation_P");
//        TotalSpinPopulation_D
        property += "," + hAtom.getProperty("TotalSpinPopulation_D");
//        LargeBondIndices
        property += "," + hAtom.getProperty("LargeBondIndices");
//        strained
        property += "," + hAtom.getProperty("strained");
//        isAttachedToHetero
        property += "," + hAtom.getProperty("isAttachedToHetero");
//        aromatic
        property += "," + hAtom.getProperty("aromatic");
//        angles
        property += "," + hAtom.getProperty("angles");
//        hybridization
        property += "," + hAtom.getProperty("hybridization");
//        AtomDegreeDescriptor
        property += "," + hAtom.getProperty("AtomDegreeDescriptor");
//        EffectiveAtomPolarizabilityDescriptor
        property += "," + hAtom.getProperty("EffectiveAtomPolarizabilityDescriptor");
//        PartialPiChargeDescriptor
        property += "," + hAtom.getProperty("PartialPiChargeDescriptor");
//        PartialSigmaChargeDescriptor
        property += "," + hAtom.getProperty("PartialSigmaChargeDescriptor");
//        PiElectronegativityDescriptor
        property += "," + hAtom.getProperty("PiElectronegativityDescriptor");
//        SigmaElectronegativityDescriptor
        property += "," + hAtom.getProperty("SigmaElectronegativityDescriptor");
//        1JCH
        property += "," + hAtom.getProperty("1JCH");
//        BondLength
        property += "," + hAtom.getProperty("BondLength");
        return property;
    }

    public static String extractCProperties(IAtom cAtom) {
        String cProperties = "";
//        valency
        cProperties += "," + cAtom.getProperty("valency");
//        numberOfValenceFreeElectrons
        cProperties += "," + cAtom.getProperty("numberOfValenceFreeElectrons");
//        sumOFBondIndices
        cProperties += "," + cAtom.getProperty("sumOFBondIndices");
//        mullikenCharge
        cProperties += "," + cAtom.getProperty("mullikenCharge");
//        AlphaSpin_S
        cProperties += "," + cAtom.getProperty("AlphaSpin_S");
//        AlphaSpin_P
        cProperties += "," + cAtom.getProperty("AlphaSpin_P");
//        AlphaSpin_D
        cProperties += "," + cAtom.getProperty("AlphaSpin_D");
//        BetaSpin_S
        cProperties += "," + cAtom.getProperty("BetaSpin_S");
//        BetaSpin_P
        cProperties += "," + cAtom.getProperty("BetaSpin_P");
//        BetaSpin_D
        cProperties += "," + cAtom.getProperty("BetaSpin_D");
//        TotalSpinPopulation_S
        cProperties += "," + cAtom.getProperty("TotalSpinPopulation_S");
//        TotalSpinPopulation_P
        cProperties += "," + cAtom.getProperty("TotalSpinPopulation_P");
//        TotalSpinPopulation_D
        cProperties += "," + cAtom.getProperty("TotalSpinPopulation_D");
//        LargeBondIndices
        cProperties += "," + cAtom.getProperty("LargeBondIndices");
//        strained
        cProperties += "," + cAtom.getProperty("strained");
//        isAttachedToHetero
        cProperties += "," + cAtom.getProperty("isAttachedToHetero");
//        aromatic
        cProperties += "," + cAtom.getProperty("aromatic");
//        hybridization
        cProperties += "," + cAtom.getProperty("hybridization");
//        AtomDegreeDescriptor
        cProperties += "," + cAtom.getProperty("AtomDegreeDescriptor");
//        EffectiveAtomPolarizabilityDescriptor
        cProperties += "," + cAtom.getProperty("EffectiveAtomPolarizabilityDescriptor");
//        PartialPiChargeDescriptor
        cProperties += "," + cAtom.getProperty("PartialPiChargeDescriptor");
//        PartialSigmaChargeDescriptor
        cProperties += "," + cAtom.getProperty("PartialSigmaChargeDescriptor");
//        PiElectronegativityDescriptor
        cProperties += "," + cAtom.getProperty("PiElectronegativityDescriptor");
//        SigmaElectronegativityDescriptor
        cProperties += "," + cAtom.getProperty("SigmaElectronegativityDescriptor");
        return cProperties;
    }
}
