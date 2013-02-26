package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.problem.TestCaseMinimizationProblem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.util.JMException;

/**
 * Class to configure and execute the NSGA-II algorithm.
 *
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba
 * "On the Effect of the Steady-State Selection Scheme in
 * Multi-Objective Genetic Algorithms"
 * 5th International Conference, EMO 2009, pp: 183-197.
 * April 2009)
 */
public class NSGAII {

    public static double MUTATION_PROBABILITY = 0.5;
    public static double CROSSOVER_PROBABILITY = 0.9;
    public static int POPULATION_SIZE = 100;
    public static int MAX_EVALUATION = 200000;

    /**
     * @param args Command line arguments.
     * @throws JMException
     * @throws IOException
     * @throws SecurityException
     * Usage: four options
     * - br.inf.ufpr.main.NSGAII populationSize maxEvaluations crossoverProbability mutationProbability
     */
    public static void main(String[] args) throws
            JMException,
            SecurityException,
            IOException,
            ClassNotFoundException {
        if (args.length < 4) {
            System.out.println("You must inform the following arguments:");
            System.out.println("\t1 - Population Size (int);");
            System.out.println("\t2 - Max Evaluations (int);");
            System.out.println("\t3 - Crossover Probability (double);");
            System.out.println("\t4 - Mutation Probability (double);");
            System.exit(0);
        } else {
            POPULATION_SIZE = Integer.valueOf(args[0]);
            MAX_EVALUATION = Integer.valueOf(args[1]);
            CROSSOVER_PROBABILITY = Double.valueOf(args[2]);
            MUTATION_PROBABILITY = Double.valueOf(args[3]);
        }

        Problem problem; // The problem to solve
        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection; // Selection operator

        HashMap parameters; // Operator parameters

        problem = new TestCaseMinimizationProblem(Reader.getDefaultReader().getProducts(), Reader.getDefaultReader().getMutants());

        algorithm = new jmetal.metaheuristics.nsgaII.NSGAII(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", POPULATION_SIZE);
        algorithm.setInputParameter("maxEvaluations", MAX_EVALUATION);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap();
        parameters.put("probability", CROSSOVER_PROBABILITY);
        crossover = CrossoverFactory.getCrossoverOperator("ProductCrossover", parameters);

        parameters = new HashMap();
        parameters.put("probability", MUTATION_PROBABILITY);
        mutation = MutationFactory.getMutationOperator("ProductMutation", parameters);

        // Selection Operator 
        parameters = new HashMap();
        parameters.put("problem", problem);
        parameters.put("populationSize", algorithm.getInputParameter("populationSize"));
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);

        int execucoes = 30;
        double[] hypervolume = new double[execucoes];

        Hypervolume qualityIndicator = new Hypervolume();

        long initTime = System.currentTimeMillis();

        File dir = new File("RESULT_" + initTime);
        if (!dir.exists()) {
            dir.mkdir();
        }

        for (int i = 0; i < execucoes; i++) {
            // Execute the Algorithm

            SolutionSet population = algorithm.execute();

            // Result messages 
            population.sortSolutions();
            population.convertObjective(1);
            population.printVariablesToFile("RESULT_" + initTime + "/VAR_" + i);
            population.printObjectivesToFile("RESULT_" + initTime + "/FUN_" + i + ".dat");

            //Hypervolume
            population.convertObjective(1);
            double[][] solutionFront = qualityIndicator.utils_.readFront("RESULT_" + initTime + "/FUN_" + i + ".dat");

            //Obtain delta value
            double value = qualityIndicator.calculateHypervolume(solutionFront, solutionFront.length, 2) * -1;

//            System.out.println("Execution " + i + " done! Total execution time: " + estimatedTime / 1000 + "s");
//            System.out.println("Estimated time remain to completion: " + ((29 - i) * (estimatedTime / 1000)) + "s");
//            System.out.println("Hypervolume " + i + ": " + value);
            hypervolume[i] = value;
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        writeHypervolume("RESULT_" + initTime + "/A_RESULT", execucoes, hypervolume, estimatedTime);
    } //main

    public static void writeHypervolume(String filePath, int execucoes, double[] hypervolume, long estimatedTime) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);

            double lowerHypervolume = Double.MAX_VALUE;
            int bestFile = 0;
            double mean = 0;
            for (int i = 0; i < execucoes; i++) {
                mean += hypervolume[i];
                if (hypervolume[i] < lowerHypervolume) {
                    lowerHypervolume = hypervolume[i];
                    bestFile = i;
                }
            }
            mean = mean / execucoes;

            bw.write("Crossover Probability: " + CROSSOVER_PROBABILITY);
            bw.newLine();
            bw.write("Mutation Probability: " + MUTATION_PROBABILITY);
            bw.newLine();
            bw.write("Population Size: " + POPULATION_SIZE);
            bw.newLine();
            bw.write("Max Evaluations: " + MAX_EVALUATION);
            bw.newLine();
            bw.write("Number of Generations: " + (MAX_EVALUATION / POPULATION_SIZE));
            bw.newLine();
            bw.write("Hypervolume Mean: " + mean);
            bw.newLine();
            bw.write("Best Pareto: Execution " + bestFile);
            bw.newLine();
            bw.write("Best Hypervolume: " + lowerHypervolume);
            bw.newLine();
            bw.newLine();
            bw.write("Execution Time: " + estimatedTime / 100);

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(NSGAII.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(NSGAII.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
} // NSGAII_main