package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import br.inf.ufpr.representation.TestCaseMinimizationProblem;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.JMException;

public class Main {

    public static void main(String[] args) {
        try {
            File file = new File(Main.class.getResource("/br/inf/ufpr/resource/input.txt").getPath());
            Reader reader = new Reader(file, " ");
            reader.read();
            TestCaseMinimizationProblem testCaseMinimizationProblem = new TestCaseMinimizationProblem(reader.getProducts(), reader.getMutants());
            testCaseMinimizationProblem.evaluate(null);
        } catch (JMException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        int i = 3;
        System.out.println(i / 2);
    }
}
