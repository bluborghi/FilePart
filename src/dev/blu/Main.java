package dev.blu;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        if (args.length==0) {
            System.out.println("no args, exiting...");
            return;
        }
        String fileDir = args[0];
        File inputFile = new File(fileDir);
        int parts = Integer.parseInt(args[1]);
        try {
            FilePartitioner.divide(inputFile,parts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
