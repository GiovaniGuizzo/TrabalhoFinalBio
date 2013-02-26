/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation.operators.crossover;

import br.inf.ufpr.representation.solution.ProductArraySolutionType;
import br.inf.ufpr.representation.variable.ProductVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import jmetal.core.Solution;
import jmetal.operators.crossover.Crossover;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 *
 * @author giovaniguizzo
 */
public class ProductCrossover extends Crossover {

    private static List VALID_TYPES = Arrays.asList(ProductArraySolutionType.class);
    private Double crossoverProbability = null;

    public ProductCrossover(HashMap<String, Object> parameters) {
        super(parameters);
        if (parameters.get("probability") != null) {
            this.crossoverProbability = (Double) parameters.get("probability");
        }
    }

    @Override
    public Object execute(Object object) throws JMException {
        Solution[] parents = (Solution[]) object;

        if (!(VALID_TYPES.contains(parents[0].getType().getClass())
                && VALID_TYPES.contains(parents[1].getType().getClass()))) {

            Configuration.logger_.log(Level.SEVERE, "ProductCrossover.execute: the solutions are not of the right type. The type should be ''Product'', but {0} and {1} are obtained", new Object[]{parents[0].getType(), parents[1].getType()});
            throw new JMException("Exception in ProductCrossover.execute()");
        } // if

        if (parents.length < 2) {
            Configuration.logger_.severe("ProductCrossover.execute: operator "
                    + "needs two parents");
            throw new JMException("Exception in ProductCrossover.execute()");
        }

        Solution[] offSpring = doCrossover(crossoverProbability,
                parents[0],
                parents[1]);

        //-> Update the offSpring solutions
        for (int i = 0; i < offSpring.length; i++) {
            offSpring[i].setCrowdingDistance(0.0);
            offSpring[i].setRank(0);
        }
        return offSpring;
    }

    private Solution[] doCrossover(Double probability, Solution parent1, Solution parent2) throws JMException {
        Solution[] offSpring = new Solution[2];
        offSpring[0] = new Solution(parent1);
        offSpring[1] = new Solution(parent2);
        try {
            if (PseudoRandom.randDouble() < probability) {
                //Criação dos novos arrays de variáveis
                int parentLength1 = parent1.getDecisionVariables().length;
                int parentLength2 = parent2.getDecisionVariables().length;
                int offspringSize = parentLength1 / 2 + parentLength2 / 2;

                ProductVariable[] newVariables;
                for (Solution solution : offSpring) {
                    if (parentLength1 == 1 || parentLength2 == 1) {
                        if (parentLength1 == parentLength2) {
                            if (Arrays.deepEquals(parent1.getDecisionVariables(), parent2.getDecisionVariables())) {
                                offspringSize = 1;
                                newVariables = new ProductVariable[offspringSize];
                                newVariables[0] = (ProductVariable) parent1.getDecisionVariables()[0].deepCopy();
                            } else {
                                offspringSize = 2;
                                newVariables = new ProductVariable[offspringSize];
                                newVariables[0] = (ProductVariable) parent1.getDecisionVariables()[0].deepCopy();
                                newVariables[1] = (ProductVariable) parent2.getDecisionVariables()[0].deepCopy();
                            }
                        } else {
                            offspringSize++;
                            newVariables = new ProductVariable[offspringSize];
                            int pos = 1;
                            for (int i = 0; i < offspringSize; i++) {
                                newVariables[i] = new ProductVariable();
                            }
                            if (parentLength1 < parentLength2) {
                                newVariables[0] = (ProductVariable) parent1.getDecisionVariables()[0].deepCopy();
                                populateVariableArray(pos, parentLength2, parent2, newVariables);
                            } else {
                                newVariables[0] = (ProductVariable) parent2.getDecisionVariables()[0].deepCopy();
                                populateVariableArray(pos, parentLength1, parent1, newVariables);
                            }
                        }
                    } else {
                        newVariables = new ProductVariable[offspringSize];
                        for (int i = 0; i < offspringSize; i++) {
                            newVariables[i] = new ProductVariable();
                        }
                        int pos = 0;
                        if (parentLength1 < parentLength2) {
                            populateVariableArray(pos, parentLength1, parent1, newVariables);
                            pos = parentLength1 / 2;
                            populateVariableArray(pos, parentLength2, parent2, newVariables);
                        } else {
                            populateVariableArray(pos, parentLength2, parent2, newVariables);
                            pos = parentLength2 / 2;
                            populateVariableArray(pos, parentLength1, parent1, newVariables);
                        }
                    }
                    solution.setDecisionVariables(newVariables);
                }
            }
        } catch (ClassCastException e1) {
            Configuration.logger_.severe("SinglePointCrossover.doCrossover: Cannot perfom "
                    + "SinglePointCrossover");
            throw new JMException("Exception in ProductCrossover.doCrossover()");
        }
        return offSpring;
    }

    private void populateVariableArray(int pos, int parentLength, Solution parent, ProductVariable[] newVariables) {
        int i = 0;
        while (i < parentLength / 2) {
            int randInt = PseudoRandom.randInt(0, parentLength - 1);
            ProductVariable temp = (ProductVariable) parent.getDecisionVariables()[randInt];
            if (Arrays.binarySearch(newVariables, temp) < 0) {
                newVariables[pos] = (ProductVariable) temp.deepCopy();
                i++;
                pos++;
                Arrays.sort(newVariables);
            }
        }
    }
}
