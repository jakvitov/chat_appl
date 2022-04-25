package Client.Encryption;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * This class is used to encrypt and messages using only targets name as a cypher key.
 * We use Crypt class with AES symmetric encryption.
 *
 * In the whole app we use calculate the encryption hashes and keys from names that we send messages to
 * Everyone holds key to messages that come to him.
 */
public class MessageCrypt {

    //A decryption key for messages that come to me
    private SecretKey myKey;
    private byte [] myInitVector;
    private Crypt crypt;

    public MessageCrypt(String name){
            this.crypt = new Crypt();
            this.myKey = crypt.getKeyFromName(name, name);
            this.myInitVector = crypt.createInitializationVector(new SecureRandom(name.getBytes(StandardCharsets.UTF_8)));
    }

    public String encryptMessage (String target, String message){
       SecretKey key = this.crypt.getKeyFromName(target, target);
       byte [] initVector = this.crypt.createInitializationVector(new SecureRandom(target.getBytes(StandardCharsets.UTF_8)));
       String cipherText = this.crypt.encrypt(message, key, initVector);
       return cipherText;
    }

    public String decryptMessage (String message){
        return this.crypt.decrypt(message, this.myKey, this.myInitVector);
    }

}
