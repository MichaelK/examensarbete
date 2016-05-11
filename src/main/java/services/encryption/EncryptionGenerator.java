package services.encryption;

/**
 * Created by Michael on 2016-02-12.
 */
public interface EncryptionGenerator {

    /**
     * Encrypt a byte array using the symmetricKey.
     * @param message
     * @param symmetricKey
     * @return
     */
    byte[] encrypt(final byte[] message, final byte[] symmetricKey);

    /**
     * Decrypt a byte array using the symmetricKey.
     * @param message
     * @param symmetricKey
     * @return
     */
    byte[] decrypt(final byte[] message, final byte[] symmetricKey);

}
