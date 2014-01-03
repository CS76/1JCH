package org.openscience.jch.diversity2222;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jch.utilities.GeneralUtility;

public class sample {

    private static int presentCount = 0;
    private static int kSubsetSize = 100;
    private static double diversityThreshold = 0.70;
    private static int diverseSetSize = 2000;

    public static void main(String[] args) throws SQLException, FileNotFoundException, CDKException {
        Initialize in = new Initialize("ccc.db", false);
        //DataImporter si = new DataImporter(in,"/Users/chandu/Desktop/zinc/6_p0.1.sdf");
        //si.importData();
        //in.generateSmiles();
        //in.generateMACCSKeys();
        
        FPCalculator fp = new FPCalculator(in,in.getRowCount("completeDataSet"));
        fp.run();
        System.exit(0);
//        int completeDataSetCount = in.getRowCount("completeDataSet");
//        int kSubSetCount = in.getRowCount("kSubSet");
//        int diverseSubSetCount = in.getRowCount("diverseSubSet");
//        
//        System.out.println("completeDataSet Count:"+ completeDataSetCount);
//        System.out.println("kSubSet Count:"+ kSubSetCount);
//        System.out.println("diverse SubSet Count:"+ diverseSubSetCount);
//        
//        //in.copyRow("completeDataSet","scrap", initialSeed);
//        int[] num = new int[completeDataSetCount];
//        for (int k = 1; k < completeDataSetCount; k++) {
//            num[k] = k;
//        }
//        
//        num = Handy.shuffleArray(num);
//        
//        in.copyRow("completeDataSet", "diverseSubSet", num[presentCount]);
//        presentCount += 1;
//        System.out.println("intialized");
//        while (presentCount < completeDataSetCount) {
//            
//            System.out.println(presentCount);
//            if (diverseSubSetCount>=diverseSetSize){
//                 System.out.println("cond1");
//                break;
//            }
//            if ((completeDataSetCount - presentCount) == 0) {
//                 System.out.println("cond2");
//                in.copyTable("recycle", "completeDataSet");
//                presentCount = 1;
//                System.out.println("completeDataSet exhausted");
//            }
//            Map<Double, Integer> map = new HashMap<Double, Integer>();
//            
//            while (map.size() <= kSubsetSize) {
//                System.out.println("innerloop:"+presentCount+","+num[presentCount]);
//                double tempDiversity = 0.0;
//                try{
//                tempDiversity = in.getMaxSum(num[presentCount]);
//                }catch(Exception e){
//                    System.out.println("error molecule ======================");
//                }
//                System.out.println("tempDiv: "+tempDiversity);
//                if (tempDiversity < diversityThreshold) {
//                    presentCount += 1;
//                    System.out.println("deleting original");
//                    //in.deleteOriginal(null, null);
//                } else {
//                    map.put(tempDiversity, num[presentCount]);
//                    System.out.println("going to kSubSet");
//                    in.copyRow("completeDataSet", "kSubSet", num[presentCount]);
//                    //in.deleteOriginal(null, null);
//                    presentCount += 1;
//                }
//            }
//            
//            double largestDiversity = Double.MIN_VALUE;
//            for (double div : map.keySet()) {
//                if (div > largestDiversity) {
//                    largestDiversity = div;
//                }
//            }
//            
//            in.copyRow("kSubSet", "diverseSubSet", map.get(largestDiversity));
//            map.remove(largestDiversity);
//            for (double div : map.keySet()) {
//                in.copyRow("kSubSet", "recycleSet", map.get(div));
//            }
//            in.deleteAllRows("kSubSet");
//            map.clear();
//            
//        }
    }
}
