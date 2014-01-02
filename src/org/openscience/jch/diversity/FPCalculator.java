package org.openscience.jch.diversity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class FPCalculator {
    public static Initialize in;
    public Connection connection = null;
    public int noOfRows = 0;
    private static int NUM_OF_TASKS = 10;

    public FPCalculator(Initialize c, int rows) {
        in = c;
        this.noOfRows = rows;
        System.out.println("FPCalculator entered");
    }

    public void run() throws SQLException {
        int batchCount = (int) Math.ceil(noOfRows / NUM_OF_TASKS);
        int start, stop;
        start = 1;
        long begTest = new java.util.Date().getTime();
        List< Future> futuresList = new ArrayList< Future>();
       // int nrOfProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService eservice = Executors.newFixedThreadPool(10);


        for (int k = 1; k <= NUM_OF_TASKS; k++) {
            stop = batchCount * k;
            System.out.println(start + "," + stop);
            futuresList.add(eservice.submit(new Task(k,start, stop,in)));
            start = stop;
        }


        eservice.shutdown();
        Object taskResult ;
        for (Future future : futuresList) {
            try {
                taskResult = future.get();
                System.out.println("result " + taskResult);
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        Double secs = new Double((new java.util.Date().getTime() - begTest) * 0.001);
        System.out.println("run time " + secs + " secs");
    }

}