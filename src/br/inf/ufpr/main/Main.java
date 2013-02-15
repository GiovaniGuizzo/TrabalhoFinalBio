package br.inf.ufpr.main;

import br.inf.ufpr.reader.Reader;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        File file = new File(Main.class.getResource("/br/inf/ufpr/resource/input.txt").getPath());
        Reader reader = new Reader(file, " ");
        reader.read();

        System.out.println(reader.getProducts().size());
        System.out.println(reader.getMutants().size());
        System.out.println(reader.getProductMutantList().size());
    }
}
