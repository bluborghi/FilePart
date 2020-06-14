
package dev.blu.model.core;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import dev.blu.model.interfaces.FileAction;

import static dev.blu.model.helpers.FileHelper.*;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.zip.CRC32;


/**
 * Class that contains file encryption/decryption methods
 * @author blubo
 *
 */
public abstract class FileCipher {
	private static int DEFAULT_BUFFER_LENGTH = 4 * 1024 * 1024;
	private int bufferLength;
	private File f_in;
	private String outputDir;
	private double percentage = 0;
	private boolean stop = false;

	/**
	 * Initializes a {@link FileCipher} 
	 * @param f The File to encrypt or decrypt
	 * @param outputDir The directory of the output file
	 */
	protected FileCipher(File f, String outputDir) {
		this.f_in = f;
		setOutputDir(outputDir);
		setBufferLength(DEFAULT_BUFFER_LENGTH);
	}

	/**
	 * Gets the output file directory
	 * @return The output file directory as a {@link String}
	 */
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * Sets the output file directory
	 * @param outputDir the new output file directory, if <code>null</code> or ermpty the same directory of the input file is used
	 */
	public void setOutputDir(String outputDir) {
		if ((outputDir == null || outputDir.isEmpty()) && getInputFile() != null)
			this.outputDir = getInputFile().getParent();
		else
			this.outputDir = outputDir;
	}

	/**
	 * Gets the buffer length
	 * @return The buffer length
	 */
	public int getBufferLength() {
		return bufferLength;
	}

	/**
	 * Sets the buffer length. This is optional, if the buffer length is unset then a default value is used
	 * @param bufferLength The buffer length
	 */
	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}

	/**
	 * Gets the input file
	 * @return The input file
	 */
	public File getInputFile() {
		return f_in;
	}

	
	private File getEncryptionOutputFile() {
		return new File(getOutputDir() + File.separator + getInputFile().getName() + ".crypt");
	}
	
	private File getDecryptionOutputFile() {
		return new File(getOutputDir() + File.separator + removeFileExtension(getInputFile().getName()));
	}

	/** 
	 * Gets the progress of the Encrytion/Decription process
	 * @return The percentage as {@link Double} in [0-100] range
	 */
	public double getPercentage() {
		return percentage;
	}

	/** 
	 * Sets the progress of the Encrytion/Decription process
	 * @param percentage The percentage as {@link Double} in [0-100] range
	 */
	protected void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Encryption method, takes a password to perform a symmetric encryption with an AES/CBC/PKCS5Padding {@link Cipher} instance
	 * @param password The password used to encrypt the File
	 * @return The encrypted {@link File}
	 * @throws IOException IOException
	 * @throws NoSuchAlgorithmException No such algorithm
	 * @throws InvalidKeySpecException Invalid Key Spec
	 * @throws NoSuchPaddingException No such padding
	 * @throws InvalidAlgorithmParameterException Invalid Algorithm Parameter
	 * @throws InvalidKeyException invalid key
	 * @throws BadPaddingException  Bad padding
	 * @throws IllegalBlockSizeException illegal block size
	 */
	public File Encrypt(char[] password)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		byte[] salt = new byte[8];
		SecureRandom srandom = new SecureRandom();
		srandom.nextBytes(salt);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 10000, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

		byte[] iv = new byte[128 / 8];
		srandom.nextBytes(iv);
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		// getOutputDir()+File.separator+fileName
		File output = getEncryptionOutputFile();
		FileOutputStream out = new FileOutputStream(output);
		out.write(salt);
		out.write(iv);

		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);

		FileInputStream in = new FileInputStream(getInputFile());
		// System.out.println("input file length: "+getFile().length());
		// System.out.println("initial output file length: "+output.length());
		processFile(ci, in, out);
		// System.out.println("final output file length: "+output.length());
		in.close();
		out.close();
		if (stop) {
			output.delete();
			return null;
		}
		return output;
	}

	/**
	 * Decryption method, takes a password to perform a symmetric decryption with an AES/CBC/PKCS5Padding {@link Cipher} instance
	 * @param password The password used to decrypt the File
	 * @return The decrypted {@link File}
	 * @throws IOException IOException
	 * @throws NoSuchAlgorithmException No such algorithm
	 * @throws InvalidKeySpecException Invalid Key Spec
	 * @throws NoSuchPaddingException No such padding
	 * @throws InvalidAlgorithmParameterException Invalid Algorithm Parameter
	 * @throws InvalidKeyException invalid key
	 * @throws BadPaddingException  Bad padding
	 * @throws IllegalBlockSizeException illegal block size
	 */
	public File Decrypt(char[] password)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		FileInputStream in = new FileInputStream(getInputFile());
		byte[] salt = new byte[8], iv = new byte[128 / 8];
		in.read(salt);
		in.read(iv);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password, salt, 10000, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

		Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
		ci.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

		File output = getDecryptionOutputFile();
		FileOutputStream out = new FileOutputStream(output);
		processFile(ci, in, out);

		in.close();
		out.close();
		if (stop) {
			output.delete();
			return null;
		}
		return output;
	}

	private void processFile(Cipher ci, InputStream in, OutputStream out)
			throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
		byte[] ibuf = new byte[getBufferLength()];
		int len;
		// System.out.println(ci.getOutputSize(getBufferLength()));
		// System.out.println(getBufferLength());
		long totalBytes = getInputFile().length();
		long bytesRead = 0;
		while ((len = in.read(ibuf)) != -1 && !stop) {
			bytesRead += len;
			setPercentage((double) 100 * bytesRead / totalBytes);
			byte[] obuf = ci.update(ibuf, 0, len);
			if (obuf != null) {
				out.write(obuf);
			}
		}
		if (stop) {
			return;
		}
		byte[] obuf = ci.doFinal();
		if (obuf != null)
			out.write(obuf);
		setPercentage(100);
	}

	public void stopAction() {
		stop = true;
	}
	
}
