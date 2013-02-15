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
import java.util.List;
import jmetal.core.Variable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author giovaniguizzo
 */
public class ProductVariableTest {

    public ProductVariableTest() {
    }

    @Test
    public void testConstructor() {
        File file = new File(Main.class.getResource("/br/inf/ufpr/resource/input.txt").getPath());
        Reader reader = new Reader(file, " ");
        reader.read();
        List<Product> products = reader.getProducts();
        List<Product> excludeProducts = new ArrayList<>(Arrays.asList(new Product(3), new Product(4), new Product(5)));
        List<Product> produtosTemp = new ArrayList<>();
        produtosTemp.add(new ProductVariable(6, products, excludeProducts).getProduct());
        produtosTemp.add(new ProductVariable(6, products, excludeProducts).getProduct());
        produtosTemp.add(new ProductVariable(6, products, excludeProducts).getProduct());
        int soma = 0;
        for (Product product : produtosTemp) {
            soma+=product.getId();
        }
        assertTrue(soma == 3);
    }
}
