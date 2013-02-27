/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation.problem;

import br.inf.ufpr.main.NSGAII;
import br.inf.ufpr.representation.solution.ProductArraySolutionType;
import br.inf.ufpr.pojo.Mutant;
import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.pojo.ProductMutant;
import br.inf.ufpr.representation.variable.ProductVariable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

/**
 *
 * @author giovaniguizzo
 */
public class TestCaseMinimizationProblem extends Problem {

    private final List<Product> products;
    private final List<Mutant> mutants;

    public TestCaseMinimizationProblem(List<Product> products, List<Mutant> mutants) {
        this.products = products;
        this.mutants = mutants;
        numberOfObjectives_ = 2;
        numberOfConstraints_ = 0;
        numberOfVariables_ = products.size();
        problemName_ = "Mutant Based Test Case Minimization";
        solutionType_ = new ProductArraySolutionType(this, products);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Mutant> getMutants() {
        return mutants;
    }

    @Override
    public void evaluate(Solution solution) throws JMException {
        ProductVariable[] decisionVariables = (ProductVariable[]) solution.getDecisionVariables();
        HashSet<Mutant> hash = new HashSet<>();
        for (ProductVariable productVariable : decisionVariables) {
            Product product = productVariable.getProduct();
            for (ProductMutant productMutant : product.getProductMutantList()) {
                if (productMutant.isKilled()) {
                    hash.add(productMutant.getMutant());
                }
            }
        }

        solution.setObjective(0, decisionVariables.length);
        solution.setObjective(1, mutants.size() - hash.size());

//        System.out.println("Fitness: " + solution.getObjective(0) + " / " + solution.getObjective(1));
    }

    public void writeHypervolumeParetoFront(String filePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write(this.getProducts().size() + " 0.0");
            bw.newLine();
            bw.write("0.0 " + this.getMutants().size());

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

    public void writeHypervolume(String filePath, int execucoes, int populationSize, int maxEvaluations, double mutationProbability, double crossoverProbability, double[] hypervolume, long estimatedTime) {
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

            bw.write("Crossover Probability: " + crossoverProbability);
            bw.newLine();
            bw.write("Mutation Probability: " + mutationProbability);
            bw.newLine();
            bw.write("Population Size: " + populationSize);
            bw.newLine();
            bw.write("Max Evaluations: " + maxEvaluations);
            bw.newLine();
            bw.write("Number of Generations: " + (maxEvaluations / populationSize));
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
}
