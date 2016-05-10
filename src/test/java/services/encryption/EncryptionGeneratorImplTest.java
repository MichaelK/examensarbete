package services.encryption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Michael on 2016-05-07.
 */
public class EncryptionGeneratorImplTest {

    private EncryptionGenerator encryptionGenerator;

    @Before
    public void init(){
        encryptionGenerator = new EncryptionGeneratorImpl();
    }

    @Test
    public void encryptAndDecrypt(){
        // Create a byte array to test with.
        byte[] message = "This is a test string to test with! It even has swedish chars like å, Ä and ö.".getBytes();
        // Create a symmetric key.
        byte[] symmetricKey = "12345678123456781234567812345678".getBytes();
        // Encrypt the message.
        byte[] encryptedMessage = encryptionGenerator.encrypt(message, symmetricKey);
        // Decrypte the encrypted message.
        byte[] decryptedMessage = encryptionGenerator.decrypt(encryptedMessage, symmetricKey);
        // Assert that the original message and decrypted are the same.
        Assert.assertArrayEquals(message, decryptedMessage);
    }
}
