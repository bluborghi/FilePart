package dev.blu;


import javax.crypto.BadPaddingException;
import javax.swing.UIManager;

import com.bulenkov.darcula.DarculaLaf;

import dev.blu.controller.AppController;
import dev.blu.model.AppModel;
import dev.blu.model.GUI.GUIModel;
import dev.blu.model.core.FileActionThread;
import dev.blu.model.core.FileCipher;
import dev.blu.model.core.FileActionConfiguration;
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

	/**
	 * starts the GUI
	 * @throws InterruptedException
	 */
    public static void main(String[] args) throws InterruptedException {
    	
    	try {
    	    UIManager.setLookAndFeel( new DarculaLaf() );
    	} catch( Exception ex ) {
    	    System.err.println( "Failed to initialize LaF" );
    	}

    	GUIModel model = new GUIModel();
    	AppView view = new AppView();
    	new AppController(model,view);
    	
    }
}
