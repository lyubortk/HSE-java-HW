package ru.hse.temp;

import java.io.*;
import java.util.Scanner;


public class Script {
    static void main(String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }

        var in = new Scanner(
                new BufferedReader(new FileReader(args[0])));

        var out = new FileWriter(args[1]);

        var valuesArray = new ArrayList<Maybe<Integer>>();

        while (in.hasNext()) {
            if (in.hasNextInt()) {
                valuesArray.add(Maybe.just(in.nextInt()));
            } else {
                valuesArray.add(Maybe.nothing());
                in.next();
            }
        }

        for (var o : valuesArray) {

        }

    }
}
