/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation.operators.mutation;

import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.representation.solution.ProductArraySolutionType;
import br.inf.ufpr.representation.variable.ProductVariable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import jmetal.core.Solution;
import jmetal.operators.mutation.Mutation;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *
 * @author giovaniguizzo
 */
public class ProductMutation extends Mutation {
    
    private Double crossoverProbability = 0D;

    public ProductMutation(HashMap<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null) {
            this.crossoverProbability = (Double) parameters.get("probability");
        }
    }

    @Override
    public Object execute(Object object) throws JMException {
        Solution solution = (Solution) object;
        removeVariableMutation(solution, crossoverProbability);
        addVariableMutation(solution, crossoverProbability);
        changeVariableMutation(solution, crossoverProbability);
        return solution;
    }

    public void removeVariableMutation(Solution solution, Double probability) {
        if (PseudoRandom.randDouble() < probability) {
            ProductVariable[] decisionVariables = (ProductVariable[]) solution.getDecisionVariables();
            if (decisionVariables.length > 1) {
                int index = PseudoRandom.randInt(0, decisionVariables.length - 1);
                ProductVariable[] newVariables = new ProductVariable[decisionVariables.length - 1];
                for (int i = 0, j = 0; j < newVariables.length; i++, j++) {
                    if (index == i) {
                        j--;
                    } else {
                        newVariables[j] = decisionVariables[i].deepCopy();
                    }
                }
                solution.setDecisionVariables(newVariables);
            }
        }
    }

    public void addVariableMutation(Solution solution, Double probability) {
        if (PseudoRandom.randDouble() < probability) {
            ProductVariable[] decisionVariables = (ProductVariable[]) solution.getDecisionVariables();
            ProductArraySolutionType solutionType = (ProductArraySolutionType) solution.getType();
            if (decisionVariables.length < solutionType.getUpperBound()) {
                ProductVariable[] newVariables = Arrays.copyOf(decisionVariables, decisionVariables.length + 1);
                List<Product> excluded = new ArrayList<>();
                for (ProductVariable productVariable : decisionVariables) {
                    excluded.add(productVariable.getProduct());
                }
                newVariables[newVariables.length - 1] = new ProductVariable(solutionType.getUpperBound(), solutionType.getProducts(), excluded);
                solution.setDecisionVariables(newVariables);
            }
        }
    }
    
    public void changeVariableMutation(Solution solution, Double probability) {
        if (PseudoRandom.randDouble() < probability) {
            ProductVariable[] decisionVariables = (ProductVariable[]) solution.getDecisionVariables();
            ProductArraySolutionType solutionType = (ProductArraySolutionType) solution.getType();
            if (decisionVariables.length < solutionType.getUpperBound()) {
                List<Product> excluded = new ArrayList<>();
                for (ProductVariable productVariable : decisionVariables) {
                    excluded.add(productVariable.getProduct());
                }
                int index = PseudoRandom.randInt(0, decisionVariables.length - 1);
                decisionVariables = Arrays.copyOf(decisionVariables, decisionVariables.length);
                decisionVariables[index] = new ProductVariable(solutionType.getUpperBound(), solutionType.getProducts(), excluded);
                solution.setDecisionVariables(decisionVariables);
            }
        }
    }
}
