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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import jmetal.metaheuristics.nsgaII.*;
import jmetal.core.*;
import jmetal.operators.crossover.*;
import jmetal.operators.mutation.*;
import jmetal.operators.selection.*;
import jmetal.problems.*;

import jmetal.util.Configuration;
import jmetal.util.JMException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.qualityIndicator.Hypervolume;

import jmetal.qualityIndicator.QualityIndicator;

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

    public static Logger logger_;      // Logger object
    public static FileHandler fileHandler_; // FileHandler object

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


        // Logger object and file to store log messages
        logger_ = Configuration.logger_;
        fileHandler_ = new FileHandler("NSGAII_main.log");
        logger_.addHandler(fileHandler_);

        problem = new TestCaseMinimizationProblem(Reader.getDefaultReader().getProducts(), Reader.getDefaultReader().getMutants());

        algorithm = new NSGAII(problem);
        //algorithm = new ssNSGAII(problem);

        // Algorithm parameters
        algorithm.setInputParameter("populationSize", 100);
        algorithm.setInputParameter("maxEvaluations", 25000);

        // Mutation and Crossover for Real codification 
        parameters = new HashMap();
        parameters.put("probability", 0.9);
        parameters.put("distributionIndex", 20.0);
        crossover = CrossoverFactory.getCrossoverOperator("ProductCrossover", parameters);

        parameters = new HashMap();
        parameters.put("probability", 0.5);
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
        for (int i = 0; i < execucoes; i++) {
            // Execute the Algorithm
            long initTime = System.currentTimeMillis();
            SolutionSet population = algorithm.execute();
            long estimatedTime = System.currentTimeMillis() - initTime;

            // Result messages 
            population.sortSolutions();
//            population.convertObjective(1);
            population.printVariablesToFile("result/VAR_" + i);
            population.printObjectivesToFile("result/FUN_" + i);

            //Hypervolume
            double[][] solutionFront = qualityIndicator.utils_.readFront("result/FUN_" + i);

            //Obtain delta value
            double value = qualityIndicator.calculateHypervolume(solutionFront, solutionFront.length, 2) * -1;

            System.out.println("Execution " + i + " done! Total execution time: " + estimatedTime / 1000 + "s");
            System.out.println("Estimated time remain to completion: " + ((29 - i) * (estimatedTime / 1000)) + "s");
            System.out.println("Hypervolume " + i + ": " + value);
            hypervolume[i] = value;
        }
        writeHypervolume("result/hypervolume", execucoes, hypervolume);
    } //main

    public static void writeHypervolume(String filePath, int execucoes, double[] hypervolume) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
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
                bw.write("Arquivo: " + i + " - Hypervolume: " + hypervolume[i]);
            }

            mean = mean / execucoes;

            bw.write("Média Hypervolume: " + mean);
            bw.write("Melhor arquivo: " + bestFile + " - Melhor Hypervolume: " + lowerHypervolume);
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
