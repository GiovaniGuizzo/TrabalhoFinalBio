/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation.problem;

import br.inf.ufpr.representation.solution.ProductArraySolutionType;
import br.inf.ufpr.pojo.Mutant;
import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.pojo.ProductMutant;
import br.inf.ufpr.representation.variable.ProductVariable;
import java.util.HashSet;
import java.util.List;
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
        solution.setObjective(1, hash.size() * -1);
        
        System.out.println("Fitness: " + solution.getObjective(0) + " / " + solution.getObjective(1));
    }
}
