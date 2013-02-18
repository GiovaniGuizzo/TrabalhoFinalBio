/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation;

import br.inf.ufpr.main.Main;
import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.reader.Reader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Solution;
import jmetal.util.JMException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author giovaniguizzo
 */
public class ProductCrossoverTest {

    public ProductCrossoverTest() {
    }

    @Test
    public void testExecute() throws Exception {
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

        Solution[] offSpringArray = null;
        try {
            offSpringArray = (Solution[]) productCrossover.execute(new Solution[]{solution, solution2});
        } catch (JMException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Gerou dois filhos?
        assertTrue(offSpringArray.length == 2);

        for (Solution offSpring : offSpringArray) {
            //O filho tem duas variaveis?
            assertTrue(offSpring.getDecisionVariables().length == 2);

            ProductVariable[] variablesTemp = (ProductVariable[]) offSpring.getDecisionVariables();
            for (ProductVariable productVariable : variablesTemp) {
                //O produto (variavel) tem ID maior que zero?
                assertTrue(productVariable.getProduct().getId() >= 0);
                //O produto (variavel) tem ID menor que o tamanho da lista de produtos?
                assertTrue(productVariable.getProduct().getId() < reader.getProducts().size());
                //O produto (variavel) está presente em um dos pais?
                assertTrue(Arrays.asList(variables).contains(productVariable)
                        || Arrays.asList(variables2).contains(productVariable));
            }
        }
    }
}
