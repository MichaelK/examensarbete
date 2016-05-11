package services.checksum;

/**
 * Takes the first 16 bytes of a hash to use as checksum.
 * @author mickal 2016-04-01
 */
public interface ChecksumGenerator {

    /**
     *
     * @param hash
     * @param bits How much of the hash that is used. Must be a multiple of 8.
     * @return
     */
    byte[] createChecksumAsByteArray(byte[] hash, int bits);

    /**
     *
     * @param hash
     * @param bits How much of the hash that is used. Must be a multiple of 8.
     * @return
     */
    String createChecksumAsHex (String hash, int bits);
}