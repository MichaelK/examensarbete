package services.encryption;

/**
 * Created by Michael on 2016-02-12.
 */
public interface EncryptionGenerator {

    byte[] encrypt(final byte[] message, final byte[] symmetricKey);

    byte[] decrypt(final byte[] message, final byte[] symmetricKey);

}
