package org.openscience.jch.nwchem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CoordExtractor {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println(extractCoordinates("C:\\Users\\CS76\\Desktop\\dataExtracted\\SP_extracted\\1_SP\\1_SP_all.cml"));
        System.out.println(extractMullikenData("C:\\Users\\CS76\\Desktop\\dataExtracted\\SP_extracted\\1_SP\\output.txt"));
    }

    public static String extractCoordinates(String filePath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String data = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            boolean coordStartTag = false;
            boolean coordActualTag = false;
            while (line != null) {
                if (line.contains(" ---------------------- Optimization converged ---------------------- ")) {
                    coordStartTag = true;
                }
                if (coordStartTag) {
                    if (line.contains("<molecule id=")) {
                        sb.append("<cml>").append("\n");
                        coordActualTag = true;
                    }
                }
                if (coordStartTag && coordActualTag) {
                    if (line.contains("</molecule>")) {
                        sb.append(line).append("\n").append("</cml>");
                        coordStartTag = false;
                        break;
                    }
                    sb.append(line).append("\n");
                }
                line = br.readLine();
            }
            //           Mulliken population analysis
            data = sb.toString();
        } finally {
            br.close();
        }
        return data;
    }

    public static String extractMullikenData(String filePath) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String data = "";
        try {
            StringBuilder mpaSb = new StringBuilder();
            String line = br.readLine();
            boolean mpaStartTag = false;
            while (line != null) {
                if (line.contains("Mulliken population analysis")) {
                    mpaStartTag = true;
                }
                if (mpaStartTag) {
                    if (line.contains("Task  times  cpu:")) {
                        mpaStartTag = false;
                    }
                    mpaSb.append(line).append("\n");
                }
                line = br.readLine();
            }
            data = mpaSb.toString();
        } finally {
            br.close();
        }
        return data;
    }
}