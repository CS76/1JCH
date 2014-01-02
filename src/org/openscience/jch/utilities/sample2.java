package org.openscience.jch.utilities;

import java.io.FileNotFoundException;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;

public class sample2 {

    public static void main(String[] args) throws NoSuchAtomException, FileNotFoundException, CDKException {
        
        
        
//        IAtomContainerSet finalCompleteDataSet = new AtomContainerSet();
//        List<IAtomContainer> completeSet = ChemUtility.readIAtomContainersFromCML("/Users/chandu/Desktop/finalNWChemCompleteData2.cml");
//        for (IAtomContainer mol : completeSet) {
//            for (IAtom atm : mol.atoms()) {
//                if (atm.getProperty("JCH") != null) {
//                    atm.setProperty("NWCHEM_1JCH", JCHAppender.extract1JCH((String) atm.getProperty("JCH"), atm, mol));
//                }
//            }
//            finalCompleteDataSet.addAtomContainer(mol);
//        }
//
//        System.out.println(finalCompleteDataSet.getAtomContainerCount());
//        ChemUtility.writeToCmlFile(finalCompleteDataSet, "/Users/chandu/Desktop/finalNWChemCompleteData3.cml");
//
//        BufferedReader br = new BufferedReader(new FileReader("/Users/chandu/Desktop/chebi.txt"));
//
//        try {
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//
//            while (line != null) {
//                 System.out.println(line);
//                line = br.readLine();
//
//            }
//        } finally {
//            br.close();
//        }
        //Filter fil = new Filter("/Users/chandu/Desktop/parameterFile.txt");
//        final List<String> elements = Arrays.asList("Zr", "Zn", "Yb", "Y", "Xe", "W", "V", "U", "Tm", "Tl", "Ti", "Th", "Te", "Tc", "Tb", "Ta", "Sr", "Sn", "Sm", "Si", "Sg", "Se", "Sc", "Sb", "S", "Ru", "Rn", "Rh", "Rf", "Re", "Rb", "Ra", "Pu", "Pt", "Pr", "Po", "Pm", "Pd", "Pb", "Pa", "P", "Os", "O", "Np", "No", "Ni", "Ne", "Nd", "Nb", "Na", "N", "Mt", "Mo", "Mn", "Mg", "Md", "Lu", "Lr", "Li", "La", "Kr", "K", "Ir", "In", "I", "Hs", "Ho", "Hg", "Hf", "He", "H", "Ge", "Gd", "Ga", "Fr", "Fm", "Fe", "F", "Eu", "Es", "Er", "Dy", "Db", "Cu", "Cs", "Cr", "Co", "Cm", "Cl", "Cf", "Ce", "Cd", "Ca", "C", "Br", "Bk", "Bi", "Bh", "Be", "Ba", "B", "Au", "At", "As", "Ar", "Am", "Al", "Ag", "Ac");
//        Map<String,Integer> elementCompoundCount = new HashMap<String,Integer>();
//        BufferedReader br = new BufferedReader(new FileReader("/Users/chandu/Desktop/eMolecules/version.smi"));
//        List<String> uniqueSymbol = new ArrayList<String>();
//        int count = 0;
//        int charLength = 0;
//        try {
//            String line = br.readLine();
//            while (line != null) {
//                //System.out.println(line.split(" ")[0]);
//                count += 1;
//                for (String indChar:ChemUtility.getAtomsListFromSmiles(line.split(" ")[0])){
//                    if (!uniqueSymbol.contains(indChar)){
//                        uniqueSymbol.add(indChar);
//                        elementCompoundCount.put(indChar,1);
//                    }else{
//                        elementCompoundCount.put(indChar,elementCompoundCount.get(indChar)+1);
//                    }
//                }
//                line = br.readLine();
//            }
//        } finally {
//            br.close();
//            System.out.println(charLength + "," + count + ":::::" + charLength / count);
//            System.out.println(uniqueSymbol);
//            for (String as: elementCompoundCount.keySet()){
//                System.out.println(as+"::::"+elementCompoundCount.get(as));
//            }
//        }
    }
}
