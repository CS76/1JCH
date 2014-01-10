/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openscience.jch.filter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.jch.utilities.ChemUtility;
import org.openscience.jch.utilities.GeneralUtility;

/**
 *
 * @author Chandrasekkhar < mailcs76[at]gmail.com / www.cs76.org>
 */
public class ParameterEngine {

    private String workingDirectory = "";
    private String smilesFile = "";
    private String parameterFile = "";

    public ParameterEngine(String workingDirectory, String pathToParameterFile, String pathToSmilesFile) throws FileNotFoundException, IOException, ClassNotFoundException {
        this.parameterFile = pathToParameterFile;
        this.smilesFile = pathToSmilesFile;
        this.workingDirectory = workingDirectory;

        Map<String, String> userSpec = new HashMap<String, String>();

        List<String> parameters = GeneralUtility.readLines(this.parameterFile);
        parameters.removeAll(Collections.singleton(""));
        for (String s : parameters) {
            System.out.println(s + "-----");
            String[] splitSpec = s.split("	");
            userSpec.put(splitSpec[0], splitSpec[1]);
        }
        System.out.println(userSpec);
        List<Parameter> p = instantiateParameters(userSpec);
        System.out.println(p.size());
        submitSmilesJob(this.smilesFile, p);
    }

    public List<Parameter> instantiateParameters(Map<String, String> parameterValues) {
        List<Parameter> paramterz = new ArrayList<Parameter>();
        ClassLoader classLoader = getClass().getClassLoader();
        for (String parameterName : parameterValues.keySet()) {
            System.out.println(parameterName);
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Parameter> c = (Class<? extends Parameter>) classLoader.loadClass("org.openscience.jch.filter." + parameterName);
                paramterz.add(instantiate(c, parameterValues.get(parameterName)));
            } catch (NoClassDefFoundError error) {
                System.out.println(error.getMessage() + "," + "1");
            } catch (ClassNotFoundException exception) {
                System.out.println(exception.getMessage() + "," + "2");
            } catch (Exception exception) {
                System.out.println(exception.getMessage() + "," + "3");
            }
        }
        return paramterz;
    }

    private Parameter instantiate(Class<? extends Parameter> c, String values) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        for (Constructor constructor : c.getConstructors()) {
            Class<?>[] params = constructor.getParameterTypes();
            System.out.println(c.getSimpleName() + Arrays.toString(params));
            if (params.length == 0) {
                return (Parameter) constructor.newInstance();
            } else if (params.length == 1) {
                return (Parameter) constructor.newInstance(values);
            }
        }
        throw new IllegalStateException("Parameter " + c.getSimpleName() + " has no usable constructors");
    }

    private void submitSmilesJob(String pathToSmilesFile, List<Parameter> parameterList) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToSmilesFile));
        int count = 0;
        List<String> tempSmilesHolder = new ArrayList<String>();
        try {
            ForkJoinPool fjPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            String line = br.readLine();
            while (line != null) {
                count += 1;
                tempSmilesHolder.add(line);

                if (count == 100000) {
                    // code to execute the screening
                    fjPool.invoke(new screenSmiles(tempSmilesHolder, parameterList));
                    count = 0;
                    tempSmilesHolder.clear();
                }
                line = br.readLine();
            }
            if (tempSmilesHolder.size() != 0) {
                fjPool.invoke(new screenSmiles(tempSmilesHolder, parameterList));
                count = 0;
                tempSmilesHolder.clear();
                
            }
        } finally {
            br.close();
        }
    }

    class screenSmiles extends RecursiveTask<List<String>> {

        private List<String> smilesToScreen = new ArrayList<String>();
        private List<Parameter> parameterList = new ArrayList<Parameter>();
        private List<String> passedSmiles = new ArrayList<String>();
        private List<String> failedSmiles = new ArrayList<String>();
        private StringBuilder passeds = new StringBuilder();

        public screenSmiles(List<String> inputSmiles, List<Parameter> screeningParameters) {
            System.out.println("new job");
            this.parameterList = screeningParameters;
            this.smilesToScreen = inputSmiles;
        }

        @Override
        protected List<String> compute() {

            int size = this.smilesToScreen.size();
            if (size > 10000) {
                int cutoff = (int) Math.ceil(size / 2);
                invokeAll(new screenSmiles(this.smilesToScreen.subList(0, cutoff), this.parameterList), new screenSmiles(this.smilesToScreen.subList(cutoff, size), this.parameterList));
            } else {
                for (String s : this.smilesToScreen) {
                    try {
                        String mol = s.split(" ")[0];
                        boolean pass = true;
                        for (Parameter pa : parameterList) {
                            if (pa.getCategory() == 1) {
                                if (!pa.test(ChemUtility.getIAtomContainerFromSmilesWAP(mol))) {
                                    pass = false;
                                    break;
                                }
                            } else if (pa.getCategory() == 2) {
                                if (!pa.test(ChemUtility.getIAtomContainerFromSmilesWAP(mol))) {
                                    pass = false;
                                    break;
                                }
                            } else if (pa.getCategory() == 3) {
                                if (!pa.test(ChemUtility.getAtomsListFromSmiles(mol))) {
                                    pass = false;
                                    break;
                                }
                            }
                        }
                        if (pass) {
                            this.passeds.append(s).append("\n");
                        } else {
                            //this.failedSmiles.add(s);
                        }
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NoSuchFieldException ex) {
                        Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CDKException ex) {
                        Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    GeneralUtility.appendToFile(this.passeds.toString(), "/Users/chandu/Desktop/filter/SP_inin.txt");
                   
                    // GeneralUtility.appendToFile(GeneralUtility.getStringFromList(this.failedSmiles), "/Users/chandu/Desktop/filter/SP_SP2_failed.txt");
                } catch (CDKException ex) {
                    Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }
    }
}