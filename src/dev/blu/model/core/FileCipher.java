package dev.blu.model.core;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import dev.blu.model.enums.SplitOption;
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

/* HOW CRC WORKS

    CODE
    byte[] chunk1 = {12,45,2,15,67};
    byte[] chunk2 = {100,9,12,76,33};
    byte[] chunk3 = {2,99,45,122,4};
    byte[] chunk4 = {12,45,2,15,67,100,9,12,76,33,2,99,45,122,4};

    CRC32 crc123 = new CRC32();
    System.out.println(crc123.getValue());
    crc123.update(chunk1);
    System.out.println(crc123.getValue());
    crc123.update(chunk2);
    System.out.println(crc123.getValue());
    crc123.update(chunk3);
    System.out.println(crc123.getValue());

    System.out.println();
    CRC32 crc4=new CRC32();
    System.out.println(crc4.getValue());
    crc4.update(chunk4);
    System.out.println(crc4.getValue());

    OUTPUT
    0
    977515892
    3215758702
    1761260175

    0
    1761260175
 */

/*
take an input file, calculate crc32, encrypt, add crc32 bytes at the end of encrypted bytes, split in multiple files

encrypt input file with key generated from user's password, hash the password and store it at the end of the output file
 */


public abstract class FileCipher {
    private static int DEFAULT_BUFFER_LENGTH = 4 * 1024 * 1024;
    private int bufferLength;
    private File f;
    private String outputDir;
    private double percentage = 0;

    protected FileCipher() {
    	//don't use this
    }
    
    protected FileCipher(File f, String outputDir) {
        setFile(f);
        setOutputDir(outputDir);
        setBufferLength(DEFAULT_BUFFER_LENGTH);
    }
/*
    public FileCipher(File f, String outputDir) {
        this (f,outputDir,DEFAULT_BUFFER_LENGTH);
    }

    public FileCipher(File f) {
        this(f, getParentDirectory(f.getAbsolutePath()), DEFAULT_BUFFER_LENGTH);
    }
*/
    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
    	if ((outputDir == null || outputDir.isEmpty()) && getFile() != null )
    		this.outputDir = getFile().getParent();
    	else 
    		this.outputDir = outputDir;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public File getFile() {
        return f;
    }

    private void setFile(File f) {
        this.f = f;
    }
   
    public double getPercentage() {
		return percentage;
	}

	protected void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public File Encrypt(char[] password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] salt = new byte[8];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(salt);
        SecretKeyFactory factory =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

        byte[] iv = new byte[128 / 8];
        srandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        //getOutputDir()+File.separator+fileName
        String fileName = getFile().getName() + ".crypt";
        File output = new File(getOutputDir()+File.separator+fileName);
        FileOutputStream out = new FileOutputStream(output);
        out.write(salt);
        out.write(iv);

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);

        FileInputStream in = new FileInputStream(getFile());
        //System.out.println("input file length: "+getFile().length());
        //System.out.println("initial output file length: "+output.length());
        processFile(ci, in, out);
        //System.out.println("final output file length: "+output.length());
        in.close();
        out.close();
        return output;
    }

    public File Decrypt(char[] password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        FileInputStream in = new FileInputStream(getFile());
        byte[] salt = new byte[8], iv = new byte[128/8];
        in.read(salt);
        in.read(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

        String fileName = removeFileExtension(getFile().getName());
        File output = new File(getOutputDir()+File.separator+fileName);
        FileOutputStream out = new FileOutputStream(output);
        processFile(ci, in, out);

        in.close();
        out.close();
        return output;
    }

    private void processFile(Cipher ci, InputStream in, OutputStream out) throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
        byte[] ibuf = new byte[getBufferLength()];
        int len;
        //System.out.println(ci.getOutputSize(getBufferLength()));
        //System.out.println(getBufferLength());
        long totalBytes = getFile().length();
        long bytesRead = 0;
        while ((len = in.read(ibuf)) != -1) {
        	bytesRead += len;
        	setPercentage((double) 100*bytesRead/totalBytes);
            byte[] obuf = ci.update(ibuf, 0, len);
            if (obuf != null) {
            	out.write(obuf);
            }
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null) out.write(obuf);
        setPercentage(100);
    }
}



