/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import jmetal.util.JMException;

/**
 *
 * @author giovaniguizzo
 */
public class ExecutionMultipleConfiguration {

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
            System.out.println("\t\tInputFile - The input file containing products and mutants.");
            System.exit(0);
        } else {
            String algorithm = args[0];
            args = Arrays.copyOfRange(args, 1, args.length);
            Properties properties = new Properties();
            for (String file : args) {
                properties.load(new FileInputStream(file));
                int populationSize = Integer.valueOf(properties.getProperty("PopulationSize"));
                int maxEvaluations = Integer.valueOf(properties.getProperty("MaxEvaluations"));
                double crossoverProbability = Double.valueOf(properties.getProperty("CrossoverProbability"));
                double mutationProbability = Double.valueOf(properties.getProperty("MutationProbability"));
                String inputFile = properties.getProperty("InputFile");
                switch (algorithm) {
                    case "NSGA-II":
                        NSGAIIExperiment nsgaII = new NSGAIIExperiment(populationSize, maxEvaluations, crossoverProbability, mutationProbability, 30, inputFile);
                        nsgaII.execute();
                        break;
                    case "SPEA2":
                        int archiveSize = Integer.valueOf(properties.getProperty("ArchiveSize"));
                        SPEA2Experiment spea2 = new SPEA2Experiment(populationSize, maxEvaluations, crossoverProbability, mutationProbability, archiveSize, 30, inputFile);
                        spea2.execute();
                        break;
                    default:
                        System.out.println("Sorry, but " + algorithm + " is not supported yet!");
                        System.exit(0);
                        break;
                }
            }
        }

    }
}
