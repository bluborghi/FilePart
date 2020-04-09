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

import dev.blu.model.helpers.FileHelper;
import dev.blu.model.interfaces.FileAction;

public class FileDecryptor extends FileCipher implements FileAction {
	private static final int MIN_PW_LENGTH = 8;
	private char[] password;
	
	public FileDecryptor(FileConfiguration config) {
		super(
				config.getFile(),
				config.getSplitConfig().getOutputDir()
			);
		password = config.getSplitConfig().getPw();
	}
	
	public FileDecryptor(File inputFile, SplitConfiguration params) {
		super(
				inputFile,
				params.getOutputDir()
			);
		password = params.getPw();
	}

	@Override
	public void start() {
		try {
			super.Decrypt(password);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException
				| IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getPercentage() {
		return super.getPercentage();
		//return 69; //nice, but not implemented
	}

	@Override
	public String checkForErrors() {
		String errors = "";
		if (getInputFile() == null || !getInputFile().exists() || getInputFile().isDirectory() || !FileHelper.getFileExtension((getInputFile().getName())).equals("crypt") )
			errors = errors.concat("File not found or invalid").concat(System.lineSeparator());
		File outputDir = new File(getOutputDir());
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
			errors = errors.concat("Output directory not found or invalid").concat(System.lineSeparator());
		if (password == null || password.length == 0)
			errors = errors.concat("Missing password").concat(System.lineSeparator());
		
		return errors;
	}

	@Override
	public File getOutputFile() {
		return new File(FileHelper.removeFileExtension(getInputFile().getAbsolutePath()));// file.txt.crypt => file.txt
	}
	
}