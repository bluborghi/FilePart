package dev.blu.model.core;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import dev.blu.model.enums.SplitOption;
import dev.blu.model.interfaces.FileAction;

public class FileEncryptor extends FileCipher implements FileAction {
	private String password;
	
	public FileEncryptor(FileConfiguration config) {
		super(
				config.getFile(),
				config.getSplitConfig().getOutputDir()
			);
		password = config.getSplitConfig().getPw().toString();
	}
	
	@Override
	public void start() {
		try {
			super.Encrypt(password);
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException
				| IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getPercentage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String checkForErrors() {
		String errors = "error checking not implemented";
		
		return errors;
	}

	@Override
	public SplitOption getSplitOption() {
		return SplitOption.Encrypt;
	}
	
}
