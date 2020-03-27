package dev.blu.model.core;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

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


public class FileCipher {
    private static int DEFAULT_BUFFER_LENGTH = 4 * 1024 * 1024;
    private int bufferLength;
    private File f;
    private String outputDir;

    public FileCipher(File f, String outputDir, int bufferLength) {
        setFile(f);
        setBufferLength(bufferLength);
    }

    public FileCipher(File f, String outputDir) {
        this (f,outputDir,DEFAULT_BUFFER_LENGTH);
    }

    public FileCipher(File f) {
        this(f, getParentDirectory(f.getAbsolutePath()), DEFAULT_BUFFER_LENGTH);
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
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

    public File Encrypt(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] salt = new byte[8];
        SecureRandom srandom = new SecureRandom();
        srandom.nextBytes(salt);
        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

        byte[] iv = new byte[128 / 8];
        srandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);

        String fileName = getFile().getAbsoluteFile() + ".crypt";
        File output = new File(fileName);
        FileOutputStream out = new FileOutputStream(output);
        out.write(salt);
        out.write(iv);

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.ENCRYPT_MODE, skey, ivspec);

        FileInputStream in = new FileInputStream(getFile());
        processFile(ci, in, out);

        in.close();
        out.close();
        return output;
    }

    public File Decrypt(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        FileInputStream in = new FileInputStream(getFile());
        byte[] salt = new byte[8], iv = new byte[128/8];
        in.read(salt);
        in.read(iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec skey = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher ci = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ci.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(iv));

        String fileName = removeFileExtension(getFile().getAbsolutePath());
        System.out.println(getFile().getAbsoluteFile());
        File output = new File(fileName);
        FileOutputStream out = new FileOutputStream(output);
        processFile(ci, in, out);

        return output;
    }

    private void processFile(Cipher ci, InputStream in, OutputStream out) throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
        byte[] ibuf = new byte[getBufferLength()];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if (obuf != null) out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null) out.write(obuf);
    }
}



