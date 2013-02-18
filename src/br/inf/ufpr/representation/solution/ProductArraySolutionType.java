/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation.solution;

import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.representation.variable.ProductVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jmetal.core.Problem;
import jmetal.core.SolutionType;
import jmetal.core.Variable;

public class ProductArraySolutionType extends SolutionType {

    private final Random random;
    private final List<Product> products;

    public ProductArraySolutionType(Problem problem, List<Product> products) {
        super(problem);
        random = new Random();
        this.products = products;
    }

    @Override
    public Variable[] createVariables() throws ClassNotFoundException {
        int numberOfVariables = random.nextInt(products.size()) + 1;
        ProductVariable[] variables = new ProductVariable[numberOfVariables];
        List<Product> excludeProducts = new ArrayList<>();
        for (int var = 0; var < numberOfVariables; var++) {
            variables[var] = new ProductVariable(products.size(), products, excludeProducts);
        }
        return variables;
    }

    public int getUpperBound() {
        return products.size();
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public ProductVariable[] copyVariables(Variable[] vars) {
        ProductVariable[] variables;

        variables = new ProductVariable[vars.length];
        for (int var = 0; var < vars.length; var++) {
            variables[var] = (ProductVariable) vars[var].deepCopy();
        } // for

        return variables;
    }
}
