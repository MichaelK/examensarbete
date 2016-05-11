package services.datapackage;

/**
 * Created by Michael on 2016-02-12.
 */
public interface DatapackageGenerator {

    /**
     * Generate a data package. A message will be compressed, encrypted with symmetricKey, and checksum will be added.
     * @param message
     * @param symmmetricKey
     * @return
     */
    String generateDatapackage(String message, byte[] symmmetricKey);

    /**
     * Open a data package that has previously been generated with "generateDatapackage". Same symmetricKey need to be used.
     * @param encryptedMessage
     * @param symmmetricKey
     * @return
     */
    String openDatapackage(String encryptedMessage, byte[] symmmetricKey);
}
