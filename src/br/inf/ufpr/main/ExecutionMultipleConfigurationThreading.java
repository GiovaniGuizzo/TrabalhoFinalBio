/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.JMException;

/**
 *
 * @author giovaniguizzo
 */
public class ExecutionMultipleConfigurationThreading {

    public static void main(String... args) throws IOException, JMException, SecurityException, ClassNotFoundException {

        if (args.length < 2) {
            System.out.println("You must inform the following arguments:");
            System.out.println("\t1. The algorithm you want to execute (NSGA-II or SPEA2).");
            System.out.println("\t2. As many property files as you want. These files must be located in the same folder as this jar. The files must contain:");
            System.out.println("\t\tPopulationSize = Population Size (int);");
            System.out.println("\t\tMaxEvaluations - Max Evaluations (int);");
            System.out.println("\t\tCrossoverProbability - Crossover Probability (double);");
            System.out.println("\t\tMutationProbability - Mutation Probability (double);");
            System.out.println("\t\tArchiveSize - Archive Size (int);");
            System.exit(0);
        } else {
            for (int i = 0; i < args.length; i++) {
                final String conf = args[i];
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ExecutionMultipleConfiguration.main(conf);
                        } catch (JMException | SecurityException | ClassNotFoundException | IOException ex) {
                            Logger.getLogger(ExecutionMultipleConfigurationThreading.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                thread.start();
            }
        }

    }
}
