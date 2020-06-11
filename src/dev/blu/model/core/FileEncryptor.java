package dev.blu.model.core;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import dev.blu.model.enums.ProcessStatus;
import dev.blu.model.interfaces.FileAction;

public class FileEncryptor extends FileCipher implements FileAction {
	private static final int MIN_PW_LENGTH = 8;
	private char[] password;
	private ProcessStatus status;
	private boolean stop = false;
	
	public FileEncryptor(FileConfiguration config) {
		super(
				config.getFile(),
				config.getSplitConfig().getOutputDir()
			);
		password = config.getSplitConfig().getPw();
		status = ProcessStatus.Ready;
	}
	
	public FileEncryptor(File inputFile, SplitConfiguration params) {
		super(
				inputFile,
				params.getOutputDir()
			);
		password = params.getPw();	
		status = ProcessStatus.Ready;
	}

	@Override
	public void start() {
		status = ProcessStatus.Running;
		try {
			super.Encrypt(password);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException
				| IOException e) {
			e.printStackTrace();
		}
		if (stop ) {
			status = ProcessStatus.Stopped;
		}else {			
			status = ProcessStatus.Completed;
		}
	}

	@Override
	public double getPercentage() {
		return super.getPercentage();
	}

	@Override
	public String checkForErrors() {
		String errors = "";
		if (getInputFile() == null || !getInputFile().exists() || getInputFile().isDirectory())
			errors = errors.concat("File not found or invalid").concat(System.lineSeparator());
		File outputDir = new File(getOutputDir());
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
			errors = errors.concat("Output directory not found or invalid").concat(System.lineSeparator());
		if (password == null || password.toString() == null || password.length == 0)
			errors = errors.concat("Missing password").concat(System.lineSeparator());
		else if (password.length < MIN_PW_LENGTH)
			errors = errors.concat("Password too short, minimum password length: "+ MIN_PW_LENGTH).concat(System.lineSeparator());
		
		return errors;
	}

	@Override
	public File getOutputFile() {
		return new File(getOutputDir()+File.separator+getInputFile().getName()+".crypt");
	}
	
	@Override
	public void clear() {
		//nothing to clear
	}

	@Override
	public ProcessStatus getActionStatus() {
		return status;
	}
	
	@Override
	public void stopAction() {
		status = ProcessStatus.Stopping;
		stop = true;
		super.stopAction();
	}
	
	
}
