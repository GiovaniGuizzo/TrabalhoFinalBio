/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation;

import br.inf.ufpr.pojo.Product;
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
        Variable[] variables = new Variable[numberOfVariables];
        List<Product> excludeProducts = new ArrayList<>();
        for (int var = 0; var < numberOfVariables; var++) {
            variables[var] = new ProductVariable(products.size(), products, excludeProducts);
        }
        return variables;
    }
}
