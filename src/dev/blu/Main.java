package dev.blu;

import java.io.*;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    public static void main(String[] args) {
        String fileDir;
        int parts;

        if (args.length==0) {
            System.out.println("no args, using default hardcoded...");
            /*
            parts = 5;
            fileDir ="D:\\FilePart\\text.txt";
            parts = 8;
            fileDir = "D:\\FilePart\\trial.jpg";
            */
            parts = 3;
            fileDir = "D:\\FilePart\\Debian.vhd";

        }
        else {
            fileDir = args[0];
            parts = Integer.parseInt(args[1]);
        }
        try {
            LocalTime t0 = LocalTime.now();
            FilePartitioner.splitByNumberOfParts(fileDir,parts);
            LocalTime t1 = LocalTime.now();
            System.out.println("Split time: "+ SECONDS.between(t0,t1));
            FilePartitioner.merge(fileDir);
            LocalTime t2 = LocalTime.now();

            System.out.println("Merge time: "+ SECONDS.between(t1,t2));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
