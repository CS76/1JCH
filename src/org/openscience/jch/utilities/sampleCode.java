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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 *
 * @author chandu
 */
public class sampleCode {

    public static void main(String[] args) throws IOException, FileNotFoundException, CDKException {
        String folderPath = "C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\category\\";
        IAtomContainerSet molSet = new AtomContainerSet();
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i]);
            List<IAtomContainer> molList = ChemUtility.readIAtomContainersFromCML(listOfFiles[i].getAbsolutePath());
            for (IAtomContainer mol : molList) {
                molSet.addAtomContainer(mol);
            }
        }
        System.out.println(molSet.getAtomContainerCount());
        //ChemUtility.mergeCML("C:\\Users\\CS76\\Desktop\\data\\", "C:\\Users\\CS76\\Desktop\\all_NWChem_1JCH_mulliken_CDK.cml");
        ChemUtility.writeToCmlFile(molSet, "C:\\Users\\CS76\\Desktop\\newS.cml");
    }
}
