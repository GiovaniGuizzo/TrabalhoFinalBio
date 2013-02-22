//  NSGAII_main.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.problem.TestCaseMinimizationProblem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import jmetal.metaheuristics.nsgaII.*;
import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.qualityIndicator.Hypervolume;

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
public class NSGAII_main {

    public static final double MUTATION_PROBABILITY = 0.5;
    public static final double CROSSOVER_PROBABILITY = 0.9;
    public static final int POPULATION_SIZE = 100;
    public static final int MAX_EVALUATION = 200000;

    /**
     * @param args Command line arguments.
     * @throws JMException
     * @throws IOException
     * @throws SecurityException
     * Usage: three options
     * - jmetal.metaheuristics.nsgaII.NSGAII_main
     * - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
     * - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
     */
    public static void main(String[] args) throws
            JMException,
            SecurityException,
            IOException,
            ClassNotFoundException {
        Problem problem; // The problem to solve
        Algorithm algorithm; // The algorithm to use
        Operator crossover; // Crossover operator
        Operator mutation; // Mutation operator
        Operator selection; // Selection operator

        HashMap parameters; // Operator parameters

        problem = new TestCaseMinimizationProblem(Reader.getDefaultReader().getProducts(), Reader.getDefaultReader().getMutants());

        algorithm = new NSGAII(problem);
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
//            population.convertObjective(1);
            population.printVariablesToFile("RESULT_" + initTime + "/VAR_" + i);
            population.printObjectivesToFile("RESULT_" + initTime + "/FUN_" + i);

            //Hypervolume
            double[][] solutionFront = qualityIndicator.utils_.readFront("RESULT_" + initTime + "/FUN_" + i);

            //Obtain delta value
            double value = qualityIndicator.calculateHypervolume(solutionFront, solutionFront.length, 2) * -1;

//            System.out.println("Execution " + i + " done! Total execution time: " + estimatedTime / 1000 + "s");
//            System.out.println("Estimated time remain to completion: " + ((29 - i) * (estimatedTime / 1000)) + "s");
//            System.out.println("Hypervolume " + i + ": " + value);
            hypervolume[i] = value;
        }
        long estimatedTime = System.currentTimeMillis() - initTime;
        writeHypervolume("RESULT_" + initTime + "/RESULT", execucoes, hypervolume, estimatedTime);
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
            bw.write("Best Pareto: " + bestFile + " - Best Hypervolume: " + lowerHypervolume);
            bw.newLine();
            bw.newLine();
            bw.write("Execution Time: " + String.format("%dh %dm %ds",
                    TimeUnit.MILLISECONDS.toHours(estimatedTime),
                    TimeUnit.MILLISECONDS.toMinutes(estimatedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(estimatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(estimatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(estimatedTime))));

            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(NSGAII_main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(NSGAII_main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
} // NSGAII_main
