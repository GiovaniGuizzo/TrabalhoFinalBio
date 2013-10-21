/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.inf.ufpr.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.util.MetricsUtil;

/**
 *
 * @author giovaniguizzo
 */
public class StandardDeviation {

    public void writeAllHypervolume(String dirPath) {
        File dir = new File(dirPath);
        MetricsUtil mu = new MetricsUtil();
        Hypervolume hypervolumeMetric = new Hypervolume();
        double[][] paretoTrueFront = mu.readFront(dir.getPath() + "/PARETO");
        double[] hypervolume = new double[30];
        for (int j = 0; j < 30; j++) {
            double[][] paretoFront = mu.readFront(dir.getPath() + "/FUN_" + j + ".dat");
            hypervolume[j] = hypervolumeMetric.hypervolume(paretoFront, paretoTrueFront, 2);
        }
        try {
            FileWriter fw = new FileWriter(dir.getPath() + "/HYPERVOLUME");
            for (double d : hypervolume) {
                fw.append(Double.toString(d) + "\n");
                fw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(StandardDeviation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
