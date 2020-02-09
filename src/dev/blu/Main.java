package dev.blu;


import javax.crypto.BadPaddingException;

import dev.blu.controller.AppController;
import dev.blu.model.FileCipher;
import dev.blu.view.AppView;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.zip.CRC32;

import static java.time.temporal.ChronoUnit.MILLIS;

public class Main {

    public static void main(String[] args) throws InterruptedException {
       /* String fileDir1, fileDir2, fileDir3;
        int parts1, parts2, parts3;


        System.out.println("no args, using default hardcoded...");

        parts1 = 7;
        fileDir1 = "D:\\FilePart\\war3.7z";
        parts2 = 5;
        fileDir2 = "D:\\FilePart\\prob.pdf";
        parts3 = 8;
        fileDir3 = "D:\\FilePart\\trial.jpg";


        Thread fpt1 = new FilePartitionerThread(fileDir1, parts1);
        Thread fpt2 = new FilePartitionerThread(fileDir2, parts2);
        Thread fpt3 = new FilePartitionerThread(fileDir3, parts3);

        LocalTime t0 = LocalTime.now();

        fpt1.start();
        fpt2.start();
        fpt3.start();
        fpt1.join();
        fpt2.join();
        fpt3.join();

        LocalTime t1 = LocalTime.now();
        long tot_size = new File(fileDir1).length() +new File(fileDir2).length() +new File(fileDir3).length();
        tot_size = tot_size / 1024 / 1024; //MiB
        System.out.println("(total "+tot_size+" MiB) time: "+ MILLIS.between(t0,t1)+"ms");*/

        /*try {

            File in = new File("/run/media/blubo/Volume/FilePart/trial.jpg");
            FileCipher fc = new FileCipher(in);
            File out = fc.Encrypt("OEUFHEOFEW");
            FileCipher fc2 = new FileCipher(out);
            fc2.Decrypt("OEUFHEOFEW");
        } catch (BadPaddingException e){
            System.err.println("WRONG PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    	
    	
    	AppView view = new AppView();
    	AppController controller = new AppController(view);
    	//AppController controller = new AppController(view);
    	view.setVisible(true);
    }
}
