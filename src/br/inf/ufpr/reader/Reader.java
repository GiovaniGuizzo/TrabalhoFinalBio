package br.inf.ufpr.reader;

import br.inf.ufpr.pojo.Mutant;
import br.inf.ufpr.pojo.Product;
import br.inf.ufpr.pojo.ProductMutant;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reader {

    private String separator;
    private File file;
    private List<Mutant> mutants;
    private List<Product> products;
    private List<ProductMutant> productMutantList;

    public Reader(String filePath, String separator) {
        this(new File(filePath), separator);
    }

    public Reader(File file, String separator) {
        this.file = file;
        this.separator = separator;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Mutant> getMutants() {
        return mutants;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<ProductMutant> getProductMutantList() {
        return productMutantList;
    }

    public boolean[][] getProductMutantListAsValueArray() {
        int columnsSize = products.size();
        int linesSize = mutants.size();
        Iterator<ProductMutant> iterator = productMutantList.iterator();

        boolean[][] array = new boolean[linesSize][columnsSize];
        int column = 0;
        int line = 0;

        while (line < linesSize) {
            while (column < columnsSize) {
                ProductMutant next = iterator.next();
                array[line][column] = next.isKilled();
                column++;
            }
            line++;
        }

        return array;
    }

    private List<Product> buildProductList(String fileLine) {
        List<Product> list = new ArrayList<>();

        List<String> asList = Arrays.asList(fileLine.split(separator));
        for (String string : asList) {
            try {
                Long valueOf = Long.valueOf(string);
                Product product = new Product(valueOf);
                product.setProductMutantList(productMutantList);
                list.add(product);
            } catch (NumberFormatException nfe) {
                //Skip if not number;
                continue;
            }
        }
        return list;
    }

    public void read() {
        try {
            Scanner scanner = new Scanner(file);

            //Products IDs
            String line = scanner.nextLine();

            productMutantList = new ArrayList<>();

            //Build product objects
            products = buildProductList(line);

            mutants = new ArrayList<>();

            //While there is something to read
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                //Tokenize
                List<String> asList = Arrays.asList(line.split(separator));
                Iterator<String> tokenIterator = asList.iterator();
                //First value is the Mutant ID
                Long id = Long.valueOf(tokenIterator.next());
                Mutant mutant = new Mutant(id);
                mutant.setProductMutantList(productMutantList);
                mutants.add(mutant);
                Iterator<Product> productIterator = products.iterator();
                while (tokenIterator.hasNext() && productIterator.hasNext()) {
                    Boolean value = Boolean.valueOf(tokenIterator.next());
                    Product product = productIterator.next();
                    ProductMutant productMutant = new ProductMutant(product, mutant, value);
                    productMutantList.add(productMutant);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}