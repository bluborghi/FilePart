package dev.blu;

import java.io.*;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String fileDir1, fileDir2, fileDir3;
        int parts1, parts2, parts3;


        System.out.println("no args, using default hardcoded...");

        parts1 = 4;
        fileDir1 = "D:\\FilePart\\Debian.iso";
        parts2 = 5;
        fileDir2 = "D:\\FilePart\\UserBenchMark.exe";
        parts3 = 8;
        fileDir3 = "D:\\FilePart\\trial.jpg";


        Thread fpt1 = new FilePartitionerThread(fileDir1, parts1);
        Thread fpt2 = new FilePartitionerThread(fileDir2, parts2);
        Thread fpt3 = new FilePartitionerThread(fileDir3, parts3);

        fpt1.start();
        fpt2.start();
        fpt3.start();

        fpt1.join();
        fpt2.join();
        fpt3.join();
    }
}
