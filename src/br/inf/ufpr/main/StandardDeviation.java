/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import java.io.File;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.util.MetricsUtil;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author giovaniguizzo
 */
public class StandardDeviation {

    public static void main(String[] args) {
        for (int i = 1; i <= 8; i++) {
            File dir = new File("/Users/giovaniguizzo/Dropbox/Trabalho Final Comp. Bioinspirada/Experimentos/CONF_" + i);
            MetricsUtil mu = new MetricsUtil();
            Hypervolume hypervolumeMetric = new Hypervolume();
            double[][] paretoTrueFront = mu.readFront(dir.getPath() + "/PARETO");
            double[] hypervolume = new double[30];
            for (int j = 0; j < 30; j++) {
                double[][] paretoFront = mu.readFront(dir.getPath() + "/FUN_" + j + ".dat");
                hypervolume[j] = hypervolumeMetric.hypervolume(paretoFront, paretoTrueFront, 2);
            }
            DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics(hypervolume);
            System.out.println(i + " " + (descriptiveStatistics.getStandardDeviation()*100000));
        }
    }
}
