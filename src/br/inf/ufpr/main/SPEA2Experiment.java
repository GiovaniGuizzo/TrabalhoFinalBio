/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.problem.TestCaseMinimizationProblem;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.DTLZ.DTLZ7;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.JMException;

/**
 *
 * @author giovaniguizzo
 */
public class SPEA2Experiment {

//    public static void main(String... args) throws JMException, SecurityException, IOException, ClassNotFoundException {
//        int execucoes = 30;
//        int populationSize = 100;
//        int maxEvaluations = 200000;
//        int archiveSize = 100;
//        double crossoverProbability = 0.9;
//        double mutationProbability = 0.1;
//
////        if (args.length < 4) {
////            System.out.println("You must inform the following arguments:");
////            System.out.println("\t1 - Population Size (int);");
////            System.out.println("\t2 - Max Evaluations (int);");
////            System.out.println("\t3 - Crossover Probability (double);");
////            System.out.println("\t4 - Mutation Probability (double);");
////            System.out.println("\t5 - Archive Size (int);");
////            System.exit(0);
////        } else {
////            populationSize = Integer.valueOf(args[0]);
////            maxEvaluations = Integer.valueOf(args[1]);
////            crossoverProbability = Double.valueOf(args[2]);
////            mutationProbability = Double.valueOf(args[3]);
////            archiveSize = Integer.valueOf(args[4]);
////        }
//
//        SPEA2Experiment spea2 = new SPEA2Experiment(populationSize, maxEvaluations, crossoverProbability, mutationProbability, archiveSize, execucoes);
////        spea2.execute();
//        spea2.executeDTLZ();
//
//    }
    private final int populationSize;
    private final int maxEvaluations;
    private final double crossoverProbability;
    private final double mutationProbability;
    private final int execucoes;
    private final int archiveSize;
    private final String inputFile;

    public SPEA2Experiment(int populationSize, int maxEvaluations, double crossoverProbability, double mutationProbability, int archiveSize, int execucoes, String inputFile) {
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.execucoes = execucoes;
        this.archiveSize = archiveSize;
        this.inputFile = inputFile;
    }
    
    public void executeDTLZ() throws JMException, ClassNotFoundException{
        Problem problem; // The problem to solve
        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection; // Selection operator

        HashMap parameters; // Operator parameters

        problem = new DTLZ7("Real", 22, 2);
        
        algorithm = new SPEA2(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);
        algorithm.setInputParameter("archiveSize", archiveSize);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap();
        parameters.put("probability", crossoverProbability);
        crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

        parameters = new HashMap();
        parameters.put("probability", mutationProbability);
        mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);

        // Selection Operator 
        parameters = new HashMap();
        parameters.put("problem", problem);
        parameters.put("populationSize", algorithm.getInputParameter("populationSize"));
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
        
        double[] hypervolume = new double[execucoes];

        long initTime = System.currentTimeMillis();
        File dir = new File("RESULT_" + initTime);
        if (!dir.exists()) {
            dir.mkdir();
        }

        QualityIndicator indicator = new QualityIndicator(problem, "./DTLZ7_PARETO");

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
            System.out.println(i + " - " + value);
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        problem.writeHypervolume(dir.getPath() + "/A_RESULT", execucoes, populationSize, maxEvaluations, mutationProbability, crossoverProbability, 0, "SPEA2 - DTLZ7", hypervolume, estimatedTime, "NONE");
    }

    public void execute() throws JMException, ClassNotFoundException, FileNotFoundException {
        TestCaseMinimizationProblem problem; // The problem to solve
        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection; // Selection operator

        HashMap parameters; // Operator parameters

        Reader reader = new Reader(inputFile, " ");
        reader.read();
        problem = new TestCaseMinimizationProblem(reader.getProducts(), reader.getMutants());

        algorithm = new SPEA2(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);
        algorithm.setInputParameter("archiveSize", archiveSize);

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
        selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);

        // Add the operators to the algorithm
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);

        double[] hypervolume = new double[execucoes];

        long initTime = System.currentTimeMillis();
        File dir = new File("RESULT_" + initTime);
        if (!dir.exists()) {
            dir.mkdir();
        }

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
            System.out.println(i + " - " + value);
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        problem.writeHypervolume(dir.getPath() + "/A_RESULT", execucoes, populationSize, maxEvaluations, mutationProbability, crossoverProbability, archiveSize, "SPEA2", hypervolume, estimatedTime, inputFile);
    }
}
