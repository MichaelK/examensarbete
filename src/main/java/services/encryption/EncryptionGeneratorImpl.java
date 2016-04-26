package services.encryption;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

/**
 * Created by Michael on 2016-02-12.
 */
public class EncryptionGeneratorImpl implements EncryptionGenerator{

    //private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionGeneratorImpl.class.getPackage().getName());
    private static final int MESSAGE_LENGTH = 32;
    private static final int[] allowedKeyLengths = {128, 256, 512, 1024};
    private static final String hexStringIV = "5B1F50F5A3CB66C51CBC5BE7A99CF31B";

    //private final int keyLength;

    @Override
    public byte[] encrypt(final byte[] message, final byte[] symmetricKey) {
        Objects.requireNonNull(message, "Parameter message cannot be null");
        Objects.requireNonNull(symmetricKey, "Parameter symmetricKey cannot be null");
        //Objects.requireNonNull(symmetricKey.length == (this.keyLength / 8), String.format("Parameter symmetricKey must be of length %d", this.keyLength));

        try {
            //Make key
            SecretKeySpec key = createKey(symmetricKey);

            //Set IV
            byte[] initializationVector = Hex.decode(hexStringIV);
            IvParameterSpec iv = new IvParameterSpec(initializationVector);

            //Set the Cipher in encryption mode
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            return cipher.doFinal(message);
        } catch (Exception e) {
            //LOGGER.error("Failed encrypting the message {0}", new Object[]{message});
            //LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public byte[] decrypt(final byte[] encrypted, final byte[] symmetricKey) {
        try {
            Objects.requireNonNull(encrypted, "Parameter encrypted cannot be null");
            Objects.requireNonNull(symmetricKey, "Parameter symmetricKey cannot be null");
            //Required.isTrue(symmetricKey.length == this.keyLength / 8, String.format("The symmetricKey length is %d but the supported length by the provider is %d ", symmetricKey.length, this.keyLength / 8));

            SecretKeySpec key = createKey(symmetricKey);

            //Set Iv
            byte[] initializationVector = Hex.decode(hexStringIV);
            IvParameterSpec iv = new IvParameterSpec(initializationVector);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            //LOGGER.error("Failed decrypting the message", e);
        }
        return null;
    }

    private SecretKeySpec createKey(final byte[] symmetricKey) {
        return new SecretKeySpec(symmetricKey, "AES");
    }

}