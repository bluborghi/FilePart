package dev.blu;


import javax.crypto.BadPaddingException;

import dev.blu.controller.AppController;
import dev.blu.model.AppModel;
import dev.blu.model.GUI.GUIModel;
import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.FileCipher;
import dev.blu.model.core.SplitConfiguration;
import dev.blu.model.enums.ByteUnit;
import dev.blu.view.AppView;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.UUID;
import java.util.Vector;
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
    	

    	
    	
    	GUIModel model = new GUIModel();
    	AppView view = new AppView();
    	AppController controller = new AppController(model,view);
    	
    	
    	
//    	UUID[] ids = new UUID[5];
    	
//    	ids[0] = model.addFile(new File("/run/media/blubo/Volume/FilePart/myFolder/mySecondOtherFolder/trial.jpg.crypt.001"));
//    	ids[1] = model.addFile(new File("/run/media/blubo/Volume/FilePart/prob.pdf"));
//    	ids[2] = model.addFile(new File("/run/media/blubo/Volume/FilePart/war3.7z"));
//    	ids[3] = model.addFile(new File("/run/media/blubo/Volume/FilePart/myFolder/myOtherFolder/war3.7z.crypt"));
//    	ids[4] = model.addFile(new File("/run/media/blubo/Volume/FilePart/myFolder/myOtherFolder/war3.7z.crypt"));
//    	
//    	model.updateConfig(ids[0], new SplitConfiguration(ids[0], SplitOption.DoNothing, 0, 0, ByteUnit.B, "freccettapun".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder"));
//     	model.updateConfig(ids[1], new SplitConfiguration(ids[1], 3, 0, ByteUnit.B, "caccamelone".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/mySecondOtherFolder"));
    	
//    	model.updateConfig(ids[1], new SplitConfiguration(ids[1], SplitOption.SplitByPartNumber, 10, 0, ByteUnit.B, null, ""));
//    	model.updateConfig(ids[2], new SplitConfiguration(ids[2], SplitOption.SplitByPartNumber, 10, 0, ByteUnit.B, null, ""));
//    	
//  	model.updateConfig(ids[2], new SplitConfiguration(ids[2], SplitOption.Encrypt, 0, 0, ByteUnit.B, "password".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/myOtherFolder/"));
//    	model.updateConfig(ids[3], new SplitConfiguration(ids[3], SplitOption.Decrypt, 0, 0, ByteUnit.B, "efoieufhefhoefhoefheof".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/myOtherFolder/"));
//    	model.updateConfig(ids[4], new SplitConfiguration(ids[4], SplitOption.Decrypt, 0, 0, ByteUnit.B, "efoieufhefhoefh".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/"));
    	
//    	model.updateConfig(ids[0], new SplitConfiguration(ids[0], SplitOption.Decrypt, 0, 0, ByteUnit.B, "freccettapun".toCharArray(), "/run/media/blubo/Volume/FilePart/myFolder/myThirdOtherFolder"));
    	
    	
//    	Vector<FileActionThread> threads = model.prepareThreads();
//    	
//    	for (FileActionThread t : threads) {
//    		if (t.hasErrors())
//    			System.err.print(t.getErrorMessage());
//    	}
//    	
//    	Vector<FileActionThread> startedThreads = model.startThreads(threads);
//    	
//    	
//    	boolean running = true;
//    	while (running) {
//    		running = false;
//    		for (FileActionThread t : startedThreads) {
//    			if (t.isAlive()) {
//    				running = true;
//    			}
//    			System.out.print(t.getFile().getName() + ": " + Math.floor(t.getPercentage()*10)/10 + " | ");
//    		}    		
//    		System.out.println();
//    		Thread.sleep(100); 
//    	}
//    	
//    	System.out.println("done");
    }
}
