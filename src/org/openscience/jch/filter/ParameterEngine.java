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
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
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

    public ParameterEngine(String workingDirectory, String pathToParameterFile, String pathToSmilesFile) throws FileNotFoundException, IOException, ClassNotFoundException, CDKException {
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
        Collections.sort(paramterz, new ParameterComparator());
        System.out.println(paramterz.toString());
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

    private void submitSmilesJob(String pathToSmilesFile, List<Parameter> parameterList) throws FileNotFoundException, IOException, CDKException {
        BufferedReader br = new BufferedReader(new FileReader(pathToSmilesFile));
        int count = 0;
        List<String> tempSmilesHolder = new ArrayList<String>();

        try {
            ForkJoinPool fjPool = new ForkJoinPool(10);
            String line = br.readLine();
            while (line != null) {
                count += 1;
                tempSmilesHolder.add(line);

                if (count == 100000) {
                    // code to execute the screening
                    screenSmiles ssm = new screenSmiles(tempSmilesHolder, parameterList);
                    fjPool.invoke(ssm);
                    count = 0;
                    tempSmilesHolder.clear();
                }
                line = br.readLine();
            }
            if (tempSmilesHolder.size() != 0) {
                screenSmiles ssm = new screenSmiles(tempSmilesHolder, parameterList);
                fjPool.invoke(ssm);
                count = 0;
                tempSmilesHolder.clear();
            }
        } finally {
            br.close();
        }
    }

    class screenSmiles extends RecursiveTask<String> {

        private List<String> smilesToScreen = new ArrayList<String>();
        private List<Parameter> parameterList = new ArrayList<Parameter>();
        private StringBuilder passeds ;
        private StringBuilder faileds ;

        public screenSmiles(List<String> inputSmiles, List<Parameter> screeningParameters) {
            System.out.println("new job");
            this.parameterList = screeningParameters;
            this.smilesToScreen = inputSmiles;
            this.passeds = new StringBuilder();
            this.faileds = new StringBuilder();
        }

        @Override
        protected String compute() {
            int size = this.smilesToScreen.size();
            if (size > 10000) {
                int cutoff = (int) Math.ceil(size / 2);
                screenSmiles ssm1 = new screenSmiles(this.smilesToScreen.subList(0, cutoff), this.parameterList);
                screenSmiles ssm2 = new screenSmiles(this.smilesToScreen.subList(cutoff, size), this.parameterList);
                this.passeds.append(ssm1.invoke());
                return (ssm2.compute() + "\n" + ssm1.join());
                //invokeAll(ssm1,ssm2);
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
                            } else if (pa.getCategory() == 3) {
                                // System.out.println(pa.getName()+"--"+ChemUtility.getAtomsListFromSmiles(mol).toString()+":::"+mol);
                                if (!pa.test(ChemUtility.getAtomsListFromSmiles(mol))) {
                                    pass = false;

                                    break;
                                }
                            } else if (pa.getCategory() == 4) {
                                // System.out.println(pa.getName()+"--"+ChemUtility.getAtomsListFromSmiles(mol).toString()+":::"+mol);
                                if (!pa.test(mol)) {
                                    pass = false;
                                    break;
                                }
                            }
                        }
                        if (pass) {
                            this.passeds.append(s).append("\n");
                        } else {
                            this.faileds.append(s).append("\n");
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
                    GeneralUtility.appendToFile(this.passeds.toString(), "/Users/chandu/Desktop/diversity/SP2_SPPassed.smi");
                    GeneralUtility.appendToFile(this.faileds.toString(), "/Users/chandu/Desktop/diversity/SP2_SPFailed.smi");
                    
                } catch (Exception ex) {
                    Logger.getLogger(ParameterEngine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }
    }
}