package ru.hse.temp;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Script {
    public static void main(String[] args) throws IOException {
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
            if (o.isPresent()) {
                out.write(o.map(Script::func).get().toString());
            } else {
                out.write("__");
            }
            out.write(" ");
        }
        out.write('\n');

        in.close();
        out.flush();
        out.close();
    }

    static private long func(int a) {
        return ((long)a)*a;
    }
}
