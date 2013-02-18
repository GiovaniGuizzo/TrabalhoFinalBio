/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.representation;

import br.inf.ufpr.pojo.Product;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import jmetal.core.Variable;

/**
 *
 * @author giovaniguizzo
 */
public class ProductVariable extends Variable implements Comparable<ProductVariable> {

    private Product product = null;
    private double upperBound;

    protected ProductVariable() {
    }

    public ProductVariable(double upperBound, List<Product> products, List<Product> excludedProducts) {
        this.upperBound = upperBound;
        Random random = new Random();
        while (product == null) {
            int nextInt = random.nextInt((int) upperBound);
            Product get = products.get(nextInt);
            product = excludedProducts.contains(get) ? null : get;
        }
        excludedProducts.add(product);
    }

    public Product getProduct() {
        return product;
    }

    protected void setProduct(Product product) {
        this.product = product;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public Variable deepCopy() {
        ProductVariable productVariable = new ProductVariable();
        productVariable.setUpperBound(this.getUpperBound());
        productVariable.setProduct(this.getProduct());
        return productVariable;
    }

    @Override
    public int compareTo(ProductVariable o) {
        if (o == null) {
            return -1;
        } else if (this.getProduct() == null && o.getProduct() == null) {
            return 0;
        } else if (o.getProduct() == null) {
            return -1;
        } else if (this.getProduct() == null) {
            return 1;
        } else {
            return Long.compare(this.getProduct().getId(), o.getProduct().getId());
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.product);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductVariable other = (ProductVariable) obj;
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        return true;
    }
}
