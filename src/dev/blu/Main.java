package dev.blu;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        String fileDir = "";
        int parts = 0;

        if (args.length==0) {
            System.out.println("no args, using default hardcoded...");

            /*
            parts = 5;
            fileDir ="D:\\FilePart\\text.txt";
            */

            parts = 12;
            fileDir = "D:\\FilePart\\trial.jpg";
        }
        else {
            fileDir = args[0];
            parts = Integer.parseInt(args[1]);
        }
        try {
            FilePartitioner.split(fileDir,parts);
            FilePartitioner.merge(fileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
