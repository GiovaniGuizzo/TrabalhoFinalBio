package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.problem.TestCaseMinimizationProblem;
import java.io.File;
import java.io.IOException;
import java.util.*;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.SolutionSet;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

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

    /**
     * @param args Command line arguments.
     * @throws JMException
     * @throws IOException
     * @throws SecurityException
     * Usage: four options
     * - br.inf.ufpr.main.NSGAII populationSize maxEvaluations crossoverProbability mutationProbability
     */
    public static void main(String... args) throws JMException, SecurityException, IOException, ClassNotFoundException {

        int execucoes = 30;
        int populationSize = 0;
        int maxEvaluations = 0;
        double crossoverProbability = 0;
        double mutationProbability = 0;

        if (args.length < 4) {
            System.out.println("You must inform the following arguments:");
            System.out.println("\t1 - Population Size (int);");
            System.out.println("\t2 - Max Evaluations (int);");
            System.out.println("\t3 - Crossover Probability (double);");
            System.out.println("\t4 - Mutation Probability (double);");
            System.exit(0);
        } else {
            populationSize = Integer.valueOf(args[0]);
            maxEvaluations = Integer.valueOf(args[1]);
            crossoverProbability = Double.valueOf(args[2]);
            mutationProbability = Double.valueOf(args[3]);
        }

        TestCaseMinimizationProblem problem; // The problem to solve
        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection; // Selection operator

        HashMap parameters; // Operator parameters

        Reader reader = new Reader(NSGAII.class.getResourceAsStream("/br/inf/ufpr/resource/input.txt"), " ");
        reader.read();
        problem = new TestCaseMinimizationProblem(reader.getProducts(), reader.getMutants());

        algorithm = new jmetal.metaheuristics.nsgaII.NSGAII(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap();
        parameters.put("probability", crossoverProbability);
        crossover = CrossoverFactory.getCrossoverOperator("ProductCrossover", parameters);

        parameters = new HashMap();
        parameters.put("probability", mutationProbability);
        mutation = MutationFactory.getMutationOperator("ProductMutation", parameters);

        // Selection Operator 
        parameters = new HashMap();
        parameters.put("problem", problem);
        parameters.put("populationSize", algorithm.getInputParameter("populationSize"));
        selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);

        double[] hypervolume = new double[execucoes];

        long initTime;

        File dir;

        do {
            initTime = System.currentTimeMillis();
            dir = new File("RESULT_" + initTime + "_" + PseudoRandom.randInt());
        } while (dir.exists());
        dir.mkdir();

        problem.writeHypervolumeParetoFront(dir.getPath() + "/PARETO");
        QualityIndicator indicator = new QualityIndicator(problem, dir.getPath() + "/PARETO");

        for (int i = 0; i < execucoes; i++) {
            // Execute the Algorithm

            SolutionSet population = algorithm.execute();

            // Result messages 
            population.sortSolutions();
            population.printVariablesToFile(dir.getPath() + "/VAR_" + i + ".dat");
            population.printObjectivesToFile(dir.getPath() + "/FUN_" + i + ".dat");

            //Hypervolume
            double value = indicator.getHypervolume(population);
            hypervolume[i] = value;
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        problem.writeHypervolume(dir.getPath() + "/A_RESULT", execucoes, populationSize, maxEvaluations, mutationProbability, crossoverProbability, hypervolume, estimatedTime);
    } //main
} // NSGAII_main