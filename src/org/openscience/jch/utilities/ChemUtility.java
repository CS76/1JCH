/* 
 * Copyright (C) 2013 Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.jch.utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.charges.Electronegativity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.invariant.EquivalentClassPartitioner;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class ChemUtility {

    /**
     *
     * @param molecule1
     * @param molecule2
     * @return
     * @throws CDKException
     */
    public static double getRMSD(IAtomContainer molecule1, IAtomContainer molecule2) throws CDKException {
        Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT, true);
        comparison.init(molecule1, molecule2, false, false);
        List<Double> lRmsd = new ArrayList<Double>();
        for (Map<Integer, Integer> map : comparison.getAllMapping()) {
            lRmsd.add(GeometryTools.getBondLengthRMSD(molecule1, molecule2, map, true));
        }
        double leastRMSD = lRmsd.get(0);
        for (double d : lRmsd) {
            if (d < leastRMSD) {
                leastRMSD = d;
            }
        }
        return leastRMSD;
    }

    /**
     *
     * @param molecule
     * @return
     */
    public static IAtomContainer isStrained(IAtomContainer molecule) {
        Object key = "strained";
        Object value1 = true;
        Object value2 = false;
        SSSRFinder ringFind = new SSSRFinder(molecule);
        IRingSet collection = ringFind.findSSSR();
        if (collection.getAtomContainerCount() > 0) {
            for (IAtomContainer eachRing : collection.atomContainers()) {
                if (eachRing.getAtomCount() > 4) {
                    for (IAtom indAtom : eachRing.atoms()) {
                        if (indAtom.getProperty("strained") == null) {
                            indAtom.setProperty(key, value2);
                        }
                    }
                }
                if (eachRing.getAtomCount() == 3 || eachRing.getAtomCount() == 4) {
                    for (IAtom indAtom : eachRing.atoms()) {
                        indAtom.setProperty(key, value1);
                    }
                }
            }
        } else {
            for (IAtom indAtom : molecule.atoms()) {
                indAtom.setProperty(key, value2);
            }
        }
        for (IAtom atom : molecule.atoms()) {
            if (atom.getProperty("strained") == null) {
                if (!atom.getSymbol().equalsIgnoreCase("h")) {
                    atom.setProperty(key, value2);
                } else if (atom.getSymbol().equalsIgnoreCase("h")) {
                    atom.setProperty(key, molecule.getConnectedAtomsList(atom).get(0).getProperty("strained"));
                }
            }
        }
        return molecule;
    }

    /**
     *
     * @param molecule
     * @return
     */
    public static IAtomContainer isHetero(IAtomContainer molecule) {
        Object key = "hetero";
        Object value1 = true;
        Object value2 = false;
        for (IAtom atom : molecule.atoms()) {
            if (!atom.getSymbol().equalsIgnoreCase("c") && !atom.getSymbol().equalsIgnoreCase("h")) {
                atom.setProperty(key, value1);
            } else {
                atom.setProperty(key, value2);
            }
        }
        return molecule;
    }

    /**
     *
     * @param molecule
     * @return
     * @throws IOException
     * @throws CDKException
     */
    public static IAtomContainer executeMinimization(IAtomContainer molecule) throws IOException, CDKException {
        String fileName = "";
        if (molecule.getID() != null) {
            fileName = molecule.getID();
        } else {
            fileName = "tempSdfFile";
        }
        Path currentRelativePath = Paths.get("");
        String tempPath = currentRelativePath.toAbsolutePath().toString();

        writeToSdfFile(molecule, tempPath + "/" + fileName + ".sdf");

        String[] params = {"/usr/local/bin/obminimize", "-n", "10000", "-sd", "-ff", "UFF", "-osdf", tempPath + "/" + fileName + ".sdf"};

        String[] minMol = execMinimization(params);

        molecule = ChemUtility.stringToIAtomContainer(minMol[0]);
        File nfile = new File(tempPath + "/temp.sdf");
        if (nfile.delete()) {
            System.out.println("temp file deleted");
        } else {
            System.out.println("cant delete the temp file");
        }

        return molecule;
    }

    /**
     *
     * @param molecule
     * @param Path
     * @return
     * @throws IOException
     * @throws CDKException
     */
    public static IAtomContainer executeMinimization(IAtomContainer molecule, String Path) throws IOException, CDKException {
        String fileName = "";
        if (molecule.getID() != null) {
            fileName = molecule.getID();
        } else {
            fileName = "tempSdfFile";
        }
        Path currentRelativePath = Paths.get("");
        String tempPath = currentRelativePath.toAbsolutePath().toString();

        writeToSdfFile(molecule, tempPath + "/" + fileName + ".sdf");

        String[] params = {"/usr/local/bin/obminimize", "-n", "10000", "-sd", "-ff", "UFF", "-osdf", tempPath + "/" + fileName + ".sdf"};

        String[] minMol = execMinimization(params);

        GeneralUtility.writeToTxtFile(minMol[0], Path + fileName + ".sdf");
        GeneralUtility.writeToTxtFile(minMol[1], Path + fileName + ".txt");


        File nfile = new File(tempPath + "/temp.sdf");
        if (nfile.delete()) {
            System.out.println("temp file deleted");
        } else {
            System.out.println("cant delete the temp file");
        }

        return molecule;
    }

    /**
     *
     * @param parameters
     * @return
     * @throws IOException
     */
    private static String[] execMinimization(String[] parameters) throws IOException {
        Process p = Runtime.getRuntime().exec(parameters);
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String molecule = "";
        String line;
        String methodDetails = "";
        while ((line = input.readLine()) != null) {
            molecule = molecule + line + "\n";
        }
        line = "";
        while ((line = stdError.readLine()) != null) {
            methodDetails = methodDetails + line + "\n";
        }
        String[] abc = {molecule, methodDetails};
        input.close();
        return abc;
    }

    /**
     *
     * @param folderPath
     * @param mergedFilePath
     * @throws CDKException
     */
    public static void mergeCML(String folderPath, String mergedFilePath) throws CDKException {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        IAtomContainerSet atomSet = new AtomContainerSet();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".cml")) {
                CMLReader CR = new CMLReader(file.getAbsolutePath());
                IChemFile cfile = CR.read(new ChemFile());
                IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(cfile).get(0);
                mol.setID(file.getName().replaceAll(".cml", ""));
                atomSet.addAtomContainer(mol);
            }
        }
        writeToCmlFile(atomSet, mergedFilePath);
    }

    /**
     *
     * @param molecule
     * @return
     * @throws CDKException
     * @throws IOException
     */
    public static String iatomcontainerToString(IAtomContainer molecule) throws CDKException, IOException {
        Writer w = new StringWriter();
        SDFWriter sdfwriter = new SDFWriter(w);
        sdfwriter.write(molecule);
        sdfwriter.close();
        return w.toString();
    }

    /**
     *
     * @param Data
     * @return
     */
    public static IAtomContainer stringToIAtomContainer(String Data) {
        IAtomContainer molecule = null;
        InputStream is = new ByteArrayInputStream(Data.getBytes());
        IteratingMolTableReader reader = new IteratingMolTableReader(is, DefaultChemObjectBuilder.getInstance(), true);
        while (reader.hasNext()) {
            molecule = reader.next();
            break;
        }
        return molecule;
    }

    /**
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static IAtomContainer readIAtomContainerFromSDF(String filePath) throws FileNotFoundException {
        File sdfFile = new File(filePath);
        IteratingMolTableReader reader = new IteratingMolTableReader(new FileInputStream(sdfFile), DefaultChemObjectBuilder.getInstance(), true);
        IAtomContainer molecule = null;
        while (reader.hasNext()) {
            molecule = reader.next();
            break;
        }
        return molecule;
    }

    /**
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     * @throws CDKException
     */
    public static IAtomContainer readIAtomContainerFromCML(String filePath) throws FileNotFoundException, CDKException {
        File cmlFile = new File(filePath);
        IAtomContainer molecule = null;
        CMLReader CR = new CMLReader(cmlFile.getPath());
        IChemFile cfile = CR.read(new ChemFile());
        molecule = ChemFileManipulator.getAllAtomContainers(cfile).get(0);
        return molecule;
    }

    /**
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     * @throws CDKException
     */
    public static List<IAtomContainer> readIAtomContainersFromCML(String filePath) throws FileNotFoundException, CDKException {
        File cmlFile = new File(filePath);
        List<IAtomContainer> moleculeSet = null;
        CMLReader CR = new CMLReader(cmlFile.getPath());
        IChemFile cfile = CR.read(new ChemFile());
        moleculeSet = ChemFileManipulator.getAllAtomContainers(cfile);
        return moleculeSet;
    }

    /**
     *
     * @param mol
     * @param filePath
     * @throws CDKException
     */
    public static void writeToCmlFile(IAtomContainer mol, String filePath) throws CDKException {
        IAtomContainer molecule = mol;
        try {
            FileWriter output = new FileWriter(filePath);
            CMLWriter cmlwriter = new CMLWriter(output);
            cmlwriter.write(molecule);
            cmlwriter.close();
            output.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param mol
     * @param filePath
     * @throws CDKException
     */
    public static void writeToCmlFile(IAtomContainerSet mol, String filePath) throws CDKException {
        IAtomContainerSet molecule = mol;
        try {
            FileWriter output = new FileWriter(filePath);
            CMLWriter cmlwriter = new CMLWriter(output);
            cmlwriter.write(molecule);
            cmlwriter.close();
            output.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param mol
     * @param filePath
     * @throws CDKException
     * @throws IOException
     */
    public static void writeToSdfFile(IAtomContainer mol, String filePath) throws CDKException, IOException {
        IAtomContainer molecule = mol;
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter output = new FileWriter(filePath);
        SDFWriter sdfwriter = new SDFWriter(output);
        sdfwriter.write(molecule);
        sdfwriter.close();
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return
     */
    public static double planeBondAngle(IAtom a, IAtom b, IAtom c, IAtom d) {
        Point3d mid = new Point3d();
        mid.x = (c.getPoint3d().x + d.getPoint3d().x) / 2;
        mid.y = (c.getPoint3d().y + d.getPoint3d().y) / 2;
        mid.z = (c.getPoint3d().z + d.getPoint3d().z) / 2;

        double ab = b.getPoint3d().distanceSquared(a.getPoint3d());
        double ac = b.getPoint3d().distanceSquared(mid);
        double bc = a.getPoint3d().distanceSquared(mid);

        return (Math.acos((ab + ac - bc) / (2 * Math.sqrt(ab) * Math.sqrt(ac))) * (180 / Math.PI));
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static double bondAngle(IAtom a, IAtom b, IAtom c) {
        double ba = 0.0;
        if (!GeometryTools.has2DCoordinates(a)) {
            double ab = a.getPoint3d().distanceSquared(b.getPoint3d());
            double ac = a.getPoint3d().distanceSquared(c.getPoint3d());
            double bc = b.getPoint3d().distanceSquared(c.getPoint3d());
            ba = (Math.acos((ab + ac - bc) / (2 * Math.sqrt(ab) * Math.sqrt(ac))) * (180 / Math.PI));
        } else {
            double ab = a.getPoint2d().distanceSquared(b.getPoint2d());
            double ac = a.getPoint2d().distanceSquared(c.getPoint2d());
            double bc = b.getPoint2d().distanceSquared(c.getPoint2d());
            ba = (Math.acos((ab + ac - bc) / (2 * Math.sqrt(ab) * Math.sqrt(ac))) * (180 / Math.PI));
        }
        return ba;
    }

    /**
     *
     * @param molecule
     * @return
     */
    public static boolean containsHeteroAtoms(IAtomContainer molecule) {
        boolean containHeteroAtoms = false;
        for (IAtom atm : molecule.atoms()) {
            if (!atm.getSymbol().equalsIgnoreCase("C") && !atm.getSymbol().equalsIgnoreCase("h")) {
                containHeteroAtoms = true;
                break;
            }
        }
        return containHeteroAtoms;
    }

    /**
     *
     * @param molecule
     * @return
     */
    public static boolean isCharged(IAtomContainer molecule) {
        boolean charged = false;
        for (IAtom atm : molecule.atoms()) {
            if (atm.getFormalCharge() != 0) {
                charged = true;
                break;
            }
        }
        return charged;
    }

    /**
     *
     * @param molecule
     * @return
     */
    public static boolean containsUnsaturation(IAtomContainer molecule) {
        boolean unsaturated = false;
        for (IBond bond : molecule.bonds()) {
            if (bond.getOrder().numeric() != 1) {
                unsaturated = true;
                break;
            }
        }
        return unsaturated;
    }

    /**
     *
     * @param mol
     * @return
     * @throws UnsupportedEncodingException
     * @throws CDKException
     * @throws IOException
     * @throws Exception
     */
    public static IAtomContainer setCharges(IAtomContainer mol) throws UnsupportedEncodingException, CDKException, IOException, Exception {
        IAtomContainer clone = mol.clone();
        Electronegativity en = new Electronegativity();
        DecimalFormat df = new DecimalFormat("#.000");
        for (int i = 0; i < mol.getAtomCount(); i++) {
            clone.getAtom(i).setProperty("EN", df.format(en.calculateSigmaElectronegativity(mol, mol.getAtom(i))));
        }
        return clone;
    }

    /**
     *
     * @param mol
     * @return
     * @throws CDKException
     */
    public static IAtomContainer setAromaticityProperty(IAtomContainer mol) throws CDKException {
        Object aromaticKey = "aromatic";
        Object value1 = true;
        Object value2 = false;

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        CDKHueckelAromaticityDetector.detectAromaticity(mol);
        for (IAtom atom : mol.atoms()) {
            if (atom.getFlag(32)) {
                //System.out.println(molAtom.getFlag(32));
                atom.setProperty(aromaticKey, value1);
            } else {
                //System.out.println(molAtom.getFlag(32));
                atom.setProperty(aromaticKey, value2);
            }
        }
        return mol;
    }

    /**
     *
     * @param indMol
     * @throws CDKException
     */
    public static void calculateDescriptor(IAtomContainer indMol) throws CDKException {
        List<String> classNames = new ArrayList<String>();
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.AtomDegreeDescriptor");
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.EffectiveAtomPolarizabilityDescriptor");
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor");
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaDescriptor");
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.PiElectronegativityDescriptor");
        classNames.add("org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor");

        DescriptorEngine engine = new DescriptorEngine(classNames, null);
        engine.process(indMol);
        for (String s : engine.getDescriptorClassNames()) {
            System.out.println(s);
        }
    }

    /**
     *
     * @param Catm
     * @param Hatm
     * @return
     */
    public static String getDescriptorVales(IAtom atom, IAtomContainer molecule) {
        String descString = "";
        Map i = atom.getProperties();
        Iterator iterator = i.keySet().iterator();
        while (iterator.hasNext()) {
            Object l = iterator.next();
            if (!(l instanceof String)) {
                descString = descString + ((DescriptorValue) i.get(l)).getValue().toString() + ",";
            }
        }
        return descString;
    }

    /**
     *
     * @param atm1
     * @param atm2
     * @return
     */
    public static double getDistance(IAtom atm1, IAtom atm2) {
        boolean has3dCoords;
        double distance = 0.0;

        if (atm1.getPoint3d() != null && atm2.getPoint3d() != null) {
            has3dCoords = true;
        } else {
            has3dCoords = false;
        }

        if (has3dCoords) {
            distance = ((Point3d) atm1.getPoint3d()).distance(atm2.getPoint3d());
        } else {
            distance = ((Point2d) atm1.getPoint2d()).distance(atm2.getPoint2d());
        }
        return distance;
    }

    /**
     *
     * @param calAtom
     * @param mol
     * @param hybtn
     * @return
     */
    public static double[] calculateBondAngles(IAtom calAtom, IAtomContainer mol) {
        int hybtn;
        if (mol.getConnectedAtomsList(calAtom).get(0).getHybridization().toString().equalsIgnoreCase("sp1")) {
            System.out.println("sp1");
            hybtn = 1;
        } else if (mol.getConnectedAtomsList(calAtom).get(0).getHybridization().toString().equalsIgnoreCase("sp2")) {
            System.out.println("sp2");
            hybtn = 2;
        } else {
            System.out.println("sp3");
            hybtn = 3;
        }
        //System.out.println();
        List<Double> deltaTheta = new ArrayList<Double>();
        List<Double> absDeltaTheta = new ArrayList<Double>();
        List<IAtom> tempList = new ArrayList<IAtom>();
        double[] values = {0.0, 0.0, 0.0, 0.0};
        if (hybtn == 3) {
            for (IAtom catm : mol.getConnectedAtomsList(calAtom)) {
                tempList.add(catm);
                for (IAtom connecAtm : mol.getConnectedAtomsList(catm)) {
                    if (!connecAtm.equals(calAtom)) {
                        tempList.add(connecAtm);
                    }
                }
            }
            for (int i = 1; i < 3; i++) {
                for (int j = i + 1; j <= 3; j++) {
                    double ba = bondAngle(tempList.get(0), tempList.get(i), tempList.get(j));
                    absDeltaTheta.add(Math.abs(109.47 - ba));
                    deltaTheta.add(109.47 - ba);
                }
            }
            double omegaDev = 0.0;
            double totalDev = 0.0;
            double totalAbsDev = 0.0;
            double maxDev = Double.MIN_VALUE;
            for (double z : absDeltaTheta) {
                totalAbsDev += z;
            }
            for (double i : deltaTheta) {
                maxDev = Math.max(maxDev, i);
                totalDev += i;
            }
            List<Double> omega = new ArrayList<Double>();
            for (int k = 1; k < 3; k++) {
                for (int l = k + 1; l <= 3; l++) {
                    omega.add(planeBondAngle(calAtom, tempList.get(0), tempList.get(k), tempList.get(l)));
                }
            }
            omegaDev = Double.MAX_VALUE;
            for (double i : omega) {
                //System.out.print(i+",");
                omegaDev = Math.min(i, omegaDev);
            }
            values[0] = totalDev;
            values[1] = totalAbsDev;
            values[2] = omegaDev;
            values[3] = maxDev;
        } else if (hybtn == 2) {
            for (IAtom catm : mol.getConnectedAtomsList(calAtom)) {
                tempList.add(catm);
                for (IAtom connecAtm : mol.getConnectedAtomsList(catm)) {
                    if (!connecAtm.equals(calAtom)) {
                        tempList.add(connecAtm);
                    }
                }
            }
            values[0] = (120.0 - bondAngle(tempList.get(0), tempList.get(1), tempList.get(2)));


        } else if (hybtn == 1) {
            for (IAtom catm : mol.getConnectedAtomsList(calAtom)) {
                tempList.add(catm);
                for (IAtom connecAtm : mol.getConnectedAtomsList(catm)) {
                    if (!connecAtm.equals(calAtom)) {
                        tempList.add(connecAtm);
                    }
                }
            }
            values[0] = bondAngle(calAtom, tempList.get(0), tempList.get(1));
        }
        return values;
    }

    public static boolean copyPropertiesBetweenMappedMolecules(IAtomContainer fromMolecule, IAtomContainer toMolecule) throws CDKException {
        Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT, true);
        comparison.init(fromMolecule, toMolecule, false, false);

        double RMSD = Double.MAX_VALUE;
        Map<Integer, Integer> bestMap = new HashMap<Integer, Integer>();
        for (Map<Integer, Integer> map : comparison.getAllMapping()) {
            double tempRMSD = GeometryTools.getAllAtomRMSD(fromMolecule, toMolecule, map, true);
            if (tempRMSD < RMSD) {
                RMSD = tempRMSD;
                bestMap = map;
            }
        }

        System.out.println(bestMap);
//        for (int a : bestMap.keySet()) {
//            IAtom atm1 = mol1.getAtom(a);
//            IAtom atm2 = mol2.getAtom(bestMap.get(a));
//            if (!atm1.getSymbol().equalsIgnoreCase(atm2.getSymbol())) {
//                System.out.println("Error ra babu");
//            }
//            if (atm1.getSymbol().equalsIgnoreCase("h")) {
//                String stringToWrite = "";
//                String[] JCHValues = (atm1.getID() + ":" + atm1.getProperty("JCH")).replace("[", "").replace("]", "").split(":")[1].split(",");
//                for (String JCH_1 : JCHValues) {
//                    String cAtmID = JCH_1.split(";")[0];
//                    IAtom cAtm = mol1.getConnectedAtomsList(atm1).get(0);
//                    
//                    //Hybridization
//                    String hybridization = ahd.calculate(cAtm, mol1).getValue().toString();
//                    if (cAtmID.replace(" ", "").equalsIgnoreCase(cAtm.getID())) {
//                        stringToWrite = ID + "," + hybridization + "," + atm1.getID() + ":" + atm1.getSymbol() + "," + JCH_1.split(";")[1].replace("Hz", "").replace(" ", "") + "," + atm2.getID() + ":" + atm1.getSymbol() + "," + atm2.getProperty("1JCH")+","+cAtm.getProperty("strained") +","+hetero+",\n";
//                        System.out.println(stringToWrite);
//
//                    }
//                }
//            }


        return Boolean.TRUE;
    }

    public void copyPropAtoms() throws CDKException, FileNotFoundException {
        String NWChemPath = "/Users/chandu/Desktop/finalNWChemCompleteData.cml";
        String ExpChemPath = "/Users/chandu/Desktop/Dataset/completeDataSet3d.cml";

        List<IAtomContainer> NWChemMolSet = ChemUtility.readIAtomContainersFromCML(NWChemPath);
        List<IAtomContainer> ExpMolSet = ChemUtility.readIAtomContainersFromCML(ExpChemPath);
        Map<IAtomContainer, IAtomContainer> mappedMol = new HashMap<IAtomContainer, IAtomContainer>();

        for (int i = 0; i < NWChemMolSet.size(); i++) {
            for (int j = 0; j < ExpMolSet.size(); j++) {
                if (NWChemMolSet.get(i).getID().replace("_NWChem_1JCH_mulliken", "").equalsIgnoreCase(ExpMolSet.get(j).getID())) {
                    //System.out.println(NWChemMolSet.get(i).getID()+":::::"+ExpMolSet.get(j).getID());
                    mappedMol.put(NWChemMolSet.get(i), ExpMolSet.get(j));
                    break;
                }
            }
        }

        System.out.println(mappedMol.size());
        for (IAtomContainer mol : mappedMol.keySet()) {
            System.out.println(mol.getID() + ":::" + mappedMol.get(mol).getID());
            for (int k = 0; k < mol.getAtomCount(); k++) {
                if (mol.getAtom(k).getProperty("JCH") != null && !mappedMol.get(mol).getAtom(k).getProperty("1JCH").toString().equalsIgnoreCase("-")) {
                    //System.out.println(mol.getAtom(k).getProperty("JCH")+"::::"+ mappedMol.get(mol).getAtom(k).getProperty("1JCH"));
                    //System.out.println(JCHAppender.extract1JCH((String)mol.getAtom(k).getProperty("JCH"),mol.getAtom(k), mol)+"::::"+ mappedMol.get(mol).getAtom(k).getProperty("1JCH"));
                    mol.getAtom(k).setProperty("Exp1JCH", mappedMol.get(mol).getAtom(k).getProperty("1JCH"));
                }
                //System.out.println(mol.getAtom(k).getID()+"::::"+mappedMol.get(mol).getAtom(k).getID());
            }
            System.out.println("==========================");
            //System.out.println(mol.getAtomCount()+":::"+mappedMol.get(mol).getAtomCount());
//            if (mol.getAtomCount() != mappedMol.get(mol).getAtomCount()){
//                 System.out.println(mol.getID()+":::"+mappedMol.get(mol).getID());
//            }


        }

        IAtomContainerSet finalCompleteDataSet = new AtomContainerSet();

        for (IAtomContainer mol : mappedMol.keySet()) {
            finalCompleteDataSet.addAtomContainer(mol);
            for (IAtom ath : mol.atoms()) {
                System.out.println(ath.getProperty("Exp1JCH"));
            }
        }

        System.out.println(finalCompleteDataSet.getAtomContainerCount());
        ChemUtility.writeToCmlFile(finalCompleteDataSet, "/Users/chandu/Desktop/finalNWChemCompleteData2.cml");
    }

    public static List<String> getAtomsListFromSmiles(String Smiles) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        List<String> atomsList = new ArrayList<String>();
        Field field = String.class.getDeclaredField("value");
        field.setAccessible(true);
        char[] chars = (char[]) field.get(Smiles);
        int len = chars.length;
        int i = 0;
        while (i < len) {
            char tempContainer = chars[i];

            if (Character.isUpperCase(tempContainer)) {
                tempContainer = Character.toLowerCase(tempContainer);
            }

            //System.out.println(tempContainer);
            switch (tempContainer) {
                case 'z':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Zr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'n') {
                        addToList(atomsList, "Zn");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "Z");
                        break;
                    }
                case 'y':
                    if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Yb");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "Y");
                        break;
                    }
                case 'x':
                    if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Xe");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "X");
                        break;
                    }

                case 'w':
                    addToList(atomsList, "W");
                    break;

                case 'v':
                    addToList(atomsList, "V");
                    break;

                case 'u':
                    addToList(atomsList, "U");
                    break;

                case 't':
                    if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Tm");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'l') {
                        addToList(atomsList, "Tl");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'i') {
                        addToList(atomsList, "Ti");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'h') {
                        addToList(atomsList, "Th");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Te");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'c') {
                        addToList(atomsList, "Tc");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Tb");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Ta");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "Z");
                        break;
                    }
                case 's':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Sr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'n') {
                        addToList(atomsList, "Sn");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Sm");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'i') {
                        addToList(atomsList, "Si");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'g') {
                        addToList(atomsList, "Sg");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Se");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'c') {
                        addToList(atomsList, "Sc");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Sb");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "S");
                        break;
                    }

                case 'r':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Ru");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'n') {
                        addToList(atomsList, "Rn");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'h') {
                        addToList(atomsList, "Rh");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'f') {
                        addToList(atomsList, "Rf");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Re");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Rb");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Ra");
                        i++;
                        break;
                    }

                case 'p':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Pu");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 't') {
                        addToList(atomsList, "Pt");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Pr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'o') {
                        addToList(atomsList, "Po");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Pm");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'd') {
                        addToList(atomsList, "Pd");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Pb");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Pa");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "P");
                        break;
                    }

                case 'o':
                    if (getNextChar(chars, i + 1) == 's') {
                        addToList(atomsList, "Os");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "O");
                        break;
                    }

                case 'n':
                    if (getNextChar(chars, i + 1) == 'p') {
                        addToList(atomsList, "Np");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'o') {
                        addToList(atomsList, "No");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'i') {
                        addToList(atomsList, "Ni");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Ne");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'd') {
                        addToList(atomsList, "Nd");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Nb");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Na");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "N");
                        break;
                    }

                case 'm':
                    if (getNextChar(chars, i + 1) == 't') {
                        addToList(atomsList, "Mt");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'o') {
                        addToList(atomsList, "Mo");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'n') {
                        addToList(atomsList, "Mn");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'g') {
                        addToList(atomsList, "Mg");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'd') {
                        addToList(atomsList, "Md");
                        i++;
                        break;
                    }

                case 'l':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Lu");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Lr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'i') {
                        addToList(atomsList, "Li");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "La");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "L");
                        i++;
                        break;
                    }

                case 'k':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Kr");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "K");
                        break;
                    }

                case 'i':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Ir");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'n') {
                        addToList(atomsList, "In");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "I");
                        break;
                    }

                case 'h':
                    if (getNextChar(chars, i + 1) == 's') {
                        addToList(atomsList, "Hs");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'o') {
                        addToList(atomsList, "Ho");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'g') {
                        addToList(atomsList, "Hg");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'f') {
                        addToList(atomsList, "Hf");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "He");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "H");
                        break;
                    }

                case 'g':
                    if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Ge");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'd') {
                        addToList(atomsList, "Gd");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Ga");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "G");
                        break;
                    }

                case 'f':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Fr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Fm");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Fe");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "F");
                        break;
                    }

                case 'e':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Eu");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 's') {
                        addToList(atomsList, "Es");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Er");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "E");
                        break;
                    }

                case 'd':
                    if (getNextChar(chars, i + 1) == 'y') {
                        addToList(atomsList, "Dy");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'b') {
                        addToList(atomsList, "Db");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "D");
                        break;
                    }


                case 'c':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Cu");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 's') {
                        addToList(atomsList, "Cs");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Cr");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'o') {
                        addToList(atomsList, "Co");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Cm");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'l') {
                        addToList(atomsList, "Cl");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'f') {
                        addToList(atomsList, "Cf");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Ce");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'd') {
                        addToList(atomsList, "Cd");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Ca");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "C");
                        break;
                    }


                case 'b':
                    if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Br");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'k') {
                        addToList(atomsList, "Bk");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'i') {
                        addToList(atomsList, "Bi");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'h') {
                        addToList(atomsList, "Bh");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'e') {
                        addToList(atomsList, "Be");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'a') {
                        addToList(atomsList, "Ba");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "B");
                        break;
                    }

                case 'a':
                    if (getNextChar(chars, i + 1) == 'u') {
                        addToList(atomsList, "Au");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 't') {
                        addToList(atomsList, "At");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 's') {
                        addToList(atomsList, "As");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'r') {
                        addToList(atomsList, "Ar");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'm') {
                        addToList(atomsList, "Am");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'l') {
                        addToList(atomsList, "Al");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'g') {
                        addToList(atomsList, "Ag");
                        i++;
                        break;
                    } else if (getNextChar(chars, i + 1) == 'c') {
                        addToList(atomsList, "Ac");
                        i++;
                        break;
                    } else {
                        addToList(atomsList, "A");
                        break;
                    }
                default:
                    addToList(atomsList, String.valueOf(tempContainer));
                    break;
            }
            i++;
        }
        return atomsList;
    }

    public static void addToList(List<String> atomsList, String Symbol) {
        if (!atomsList.contains(Symbol)) {
            atomsList.add(Symbol);
        }
    }

    public static char getNextChar(char[] charList, int i) {
        char tempChar = 0;
        if (i < (charList.length - 1)) {
            tempChar = charList[i];
        }
        return tempChar;
    }

    public static String getSMILES(IAtomContainer mol) throws CDKException {
        SmilesGenerator sg = new SmilesGenerator();
        sg.setUseAromaticityFlag(true);
        String smiles = sg.create(mol);
        return smiles;
    }

    public static Map<Integer, List<IAtom>> getEquivalentAtoms(IAtomContainer mol) throws NoSuchAtomException {
        EquivalentClassPartitioner ec = new EquivalentClassPartitioner(mol);
        int[] a = ec.getTopoEquivClassbyHuXu(mol);
        //System.out.println(GeneralUtility.arrayToString(a));
        Map<Integer, List<IAtom>> map = new HashMap<Integer, List<IAtom>>();
        for (int i = 1; i < a.length; i++) {
            List<IAtom> atm = map.get(a[i]);
            if (atm == null) {
                atm = new ArrayList<IAtom>();
                map.put(a[i], atm);
            }
            atm.add(mol.getAtom(i - 1));
        }
        return map;
    }

    public static IAtomContainer getIAtomContainerFromSmiles(String smiles) {
        IAtomContainer mol = new AtomContainer();
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        try {
            mol = sp.parseSmiles(smiles);
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(ChemUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mol;
    }

    public static IAtomContainer getIAtomContainerFromSmilesWAP(String smiles) throws CDKException {
        IAtomContainer mol = new AtomContainer();
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        SmilesParser sp = new SmilesParser(builder);
        try {
            mol = sp.parseSmiles(smiles);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            //CDKHydrogenAdder.getInstance(builder).addImplicitHydrogens(mol);
        } catch (InvalidSmilesException ex) {
            Logger.getLogger(ChemUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mol;
    }
}