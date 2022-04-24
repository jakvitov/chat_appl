package Client.Encryption;

import com.sun.security.jgss.GSSUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A basic encryption class that will handle message encryption for this software
 */

public class Crypt {

    private final String algo = "AES/CBC/PKCS5PADDING";

    //A method used to generate our encryption key using the given seed
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

    public byte [] createInitializationVector (SecureRandom random){
        byte[] initVector = new byte[16];
        random.nextBytes(initVector);
        return initVector;
    }

    public byte[] encrypt(byte [] source, SecretKey key, byte [] initVector){

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
            return cipher.doFinal(source);
        }
        catch (Exception e){
            System.out.println("Error while encrypting the message.");
            return null;
        }
    }

    public String decrypt(byte [] cypherText, SecretKey key, byte [] initVector){

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
            byte [] result = cipher.doFinal(cypherText);
            return new String(result);
        }
        catch (Exception e){
            System.out.println("Error while encrypting the message.");
            return null;
        }
    }

    public static void main(String[] args) {
        String a = "Encryption and Decryption using the symmetric key: The following steps can be followed in order to perform the encryption and decryption. ";
        byte [] message = a.getBytes(StandardCharsets.UTF_8);
        Crypt cr = new Crypt();
        String hash = "9919191999999999999999999999991919191999";
        SecretKey key = cr.createKey("9919191999999999999999999999991919191999");
        byte [] vector = cr.createInitializationVector(new SecureRandom(hash.getBytes(StandardCharsets.UTF_8)));

        byte [] cypherText = cr.encrypt(message, key, vector);
        System.out.println(new String(cypherText));

        System.out.println(cr.decrypt(cypherText, key, vector));

    }

}
