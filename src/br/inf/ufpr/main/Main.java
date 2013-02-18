package br.inf.ufpr.main;

import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.ProductCrossover;
import br.inf.ufpr.representation.ProductVariable;
import br.inf.ufpr.representation.TestCaseMinimizationProblem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.util.JMException;

public class Main {

    public static void main(String[] args) {
        File file = new File(Main.class.getResource("/br/inf/ufpr/resource/input.txt").getPath());
        Reader reader = new Reader(file, " ");
        reader.read();

        TestCaseMinimizationProblem testCaseMinimizationProblem = new TestCaseMinimizationProblem(reader.getProducts(), reader.getMutants());
        List<Product> excluded = new ArrayList<>();

        ProductVariable[] variables = new ProductVariable[2];
        variables[0] = new ProductVariable(reader.getProducts().size(), reader.getProducts(), excluded);
        variables[1] = new ProductVariable(reader.getProducts().size(), reader.getProducts(), excluded);
        Solution solution = new Solution(testCaseMinimizationProblem, variables);

        ProductVariable[] variables2 = new ProductVariable[2];
        variables2[0] = new ProductVariable(reader.getProducts().size(), reader.getProducts(), excluded);
        variables2[1] = new ProductVariable(reader.getProducts().size(), reader.getProducts(), excluded);
        Solution solution2 = new Solution(testCaseMinimizationProblem, variables2);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("probability", 1D);
        ProductCrossover productCrossover = new ProductCrossover(parameters);

        Solution[] offSpring = null;
        try {
            offSpring = (Solution[]) productCrossover.execute(new Solution[]{solution, solution2});
        } catch (JMException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Parent 1:");
        System.out.println(((ProductVariable[]) solution.getDecisionVariables())[0].getProduct().getId());
        System.out.println(((ProductVariable[]) solution.getDecisionVariables())[1].getProduct().getId());
        
        System.out.println("Parent 2:");
        System.out.println(((ProductVariable[]) solution2.getDecisionVariables())[0].getProduct().getId());
        System.out.println(((ProductVariable[]) solution2.getDecisionVariables())[1].getProduct().getId());
        
        System.out.println("Offspring:");
        System.out.println(((ProductVariable[]) offSpring[0].getDecisionVariables())[0].getProduct().getId());
        System.out.println(((ProductVariable[]) offSpring[0].getDecisionVariables())[1].getProduct().getId());
    }
}
