/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import jmetal.util.JMException;

/**
 *
 * @author giovaniguizzo
 */
public class NSGAIIMultipleConfiguration {

    public static void main(String... args) throws IOException, JMException, SecurityException, ClassNotFoundException {

        if (args.length < 1) {
            System.out.println("You must inform the following arguments:");
            System.out.println("\t1. As many property files as you want. These files must be located in the same folder as this jar. The files must contain:");
            System.out.println("\t\tPopulationSize = Population Size (int);");
            System.out.println("\t\tMaxEvaluations - Max Evaluations (int);");
            System.out.println("\t\tCrossoverProbability - Crossover Probability (double);");
            System.out.println("\t\tMutationProbability - Mutation Probability (double);");
            System.exit(0);
        } else {
            Properties properties = new Properties();
            for (String file : args) {
                properties.load(new FileInputStream(file));
                String populationSize = properties.getProperty("PopulationSize");
                String maxEvaluations = properties.getProperty("MaxEvaluations");
                String crossoverProbability = properties.getProperty("CrossoverProbability");
                String mutationProbability = properties.getProperty("MutationProbability");
                NSGAII.main(populationSize, maxEvaluations, crossoverProbability, mutationProbability);
            }
        }

    }
}
