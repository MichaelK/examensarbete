package services.encryption;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

/**
 * Created by Michael on 2016-04-01.
 */
public class EncryptionGeneratorImpl implements EncryptionGenerator{

    // Static Initialization Vector. Needed for AES-CBC.
    private static final String HEX_STRING_IV = "5B1F50F5A3CB66C51CBC5BE7A99CF31B";

    @Override
    public byte[] encrypt(final byte[] message, final byte[] symmetricKey) {
        Objects.requireNonNull(message, "Parameter message cannot be null");
        Objects.requireNonNull(symmetricKey, "Parameter symmetricKey cannot be null");

        try {
            //Make key
            SecretKeySpec key = createKey(symmetricKey);

            //Set IV
            byte[] initializationVector = Hex.decode(HEX_STRING_IV);
            IvParameterSpec iv = new IvParameterSpec(initializationVector);

            //Set the Cipher in encryption mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            return cipher.doFinal(message);
        } catch (Exception e) {
            System.out.println("Error trying to encrypt. " + e);
            // Maybe Logger in the future.
        }
        return null;
    }

    @Override
    public byte[] decrypt(final byte[] encrypted, final byte[] symmetricKey) {
        try {
            Objects.requireNonNull(encrypted, "Parameter encrypted cannot be null");
            Objects.requireNonNull(symmetricKey, "Parameter symmetricKey cannot be null");

            //Make key
            SecretKeySpec key = createKey(symmetricKey);

            //Set Iv
            byte[] initializationVector = Hex.decode(HEX_STRING_IV);
            IvParameterSpec iv = new IvParameterSpec(initializationVector);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            System.out.println("Error trying to decrypt. " + e);
            // Maybe Logger in the future.
        }
        return null;
    }

    // Method to create key.
    private SecretKeySpec createKey(final byte[] symmetricKey) {
        return new SecretKeySpec(symmetricKey, "AES");
    }

}