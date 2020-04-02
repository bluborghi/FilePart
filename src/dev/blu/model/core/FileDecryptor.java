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

import dev.blu.model.enums.SplitOption;
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
		if (getFile() == null || !getFile().exists() || getFile().isDirectory() || !FileHelper.getFileExtension((getFile().getName())).equals("crypt") )
			errors = errors.concat("File not found or invalid").concat(System.lineSeparator());
		File outputDir = new File(getOutputDir());
		if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory())
			errors = errors.concat("Output directory not found or invalid").concat(System.lineSeparator());
		if (password == null || password.length == 0)
			errors = errors.concat("Missing password").concat(System.lineSeparator());
		
		return errors;
	}

	@Override
	public SplitOption getSplitOption() {
		return SplitOption.Decrypt;
	}
	
}