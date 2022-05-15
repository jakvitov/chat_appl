package Client.Encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * This class is used for basic encryption and its setup. We translate here only arrays of bytes
 */

public class Crypt {

    private final String algo = "AES/CBC/PKCS5Padding";

    //Given a hash string generate secret key from it
    public SecretKey createKey (String hash){

        byte [] inputHash = hash.getBytes(StandardCharsets.UTF_8);

        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance("AES");
        }
        catch (NoSuchAlgorithmException NSAE){
            System.out.println("Error while creating the key. Your further communication is not protected!");
            return null;
        }

        SecureRandom random = new SecureRandom(inputHash);
        keygen.init(256, random);
        return keygen.generateKey();
    }

    //Given an username and salt, return a secret key generated from them
    public SecretKey getKeyFromName(String name, String salt){
        try {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(name.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey finalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return finalKey;
        }
        catch (Exception e){
            System.out.println("Error while initialising the key factory!");
        }
        return null;
    }

    //Given a client name, create initialization vector for the AES algo and return it as a  byte array,
    //In case the vector is too short, fill it to match 16 bytes long
    public byte [] createInitializationVector (String name){
        byte [] hash = Integer.toString(name.hashCode()).getBytes();
        byte [] result = new byte[16];

        for (int i = 0; i < Math.min(17, hash.length); i ++){
            result[i] = hash[i];
        }

        //If the hash is too short we need to expand it to 16 characters
        if (hash.length < 16){
            for (int i = hash.length - 1; i < 16; i++){
                result[i] = '1';
            }
        }
        return result;
    }

    //Given a  string, secret key and init Vector - return the encrypted string
    public String encrypt(String source, SecretKey key, byte [] initVector){

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(this.algo);
        }
        catch (NoSuchPaddingException NSPE){
            System.out.println("Error while initialising the cypher");
            return null;
        }
        catch (NoSuchAlgorithmException NSAE){
            System.out.println("Error while initialising the cypher");
            return null;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);

        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            byte [] result = cipher.doFinal(source.getBytes());
            return Base64.getEncoder().encodeToString(result);
        }
        catch (Exception e){
            System.out.println("Error while encrypting the message.");
            return null;
        }
    }

    //Given a  string, secret key and init Vector - return the decrypted string
    public String decrypt(String cipherText, SecretKey key, byte [] initVector){

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(this.algo);
        }
        catch (NoSuchPaddingException NSPE){
            System.out.println("Error while initialising the cypher");
            return null;
        }
        catch (NoSuchAlgorithmException NSAE){
            System.out.println("Error while initialising the cypher");
            return null;
        }

        IvParameterSpec ivParameterSpec = new IvParameterSpec(initVector);

        try {
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte [] result = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(result);
        }
        catch (Exception e){
            System.out.println("Error while decrypting the message.");
            return null;
        }
    }
}
