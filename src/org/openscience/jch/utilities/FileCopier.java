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
import java.io.IOException;
import org.openscience.cdk.exception.CDKException;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class FileCopier {

    public static void main(String[] args) throws IOException, CDKException {
        File folder = new File("C:\\Users\\CS76\\Desktop\\dataExtracted\\1jch\\SP2_SP_extracted");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].getName().equalsIgnoreCase(".DS_Store")) {
                File subFile = listOfFiles[i];
                String fileName = subFile.getName();
                File source = new File(subFile + "/" + fileName + "_NWChem_1JCH_mulliken_CDK.cml");
                File destinationFolder = new File("C:\\Users\\CS76\\Desktop\\data\\" + fileName + "_NWChem_1JCH_mulliken_CDK.cml");
                GeneralUtility.copyFile(source, destinationFolder);
            }
        } 
    }
}
