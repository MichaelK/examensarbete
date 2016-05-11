package services.checksum;

import java.util.Objects;

/**
 * Takes the first 16 bytes of a hash to use as checksum.
 * @author mickal 2016-02-04
 */
public class ChecksumGeneratorImpl implements ChecksumGenerator {

    /**
     *
     * @param hash hash in hex string.
     * @param bits How much of the hash that is used. Must be a multiple of 8.
     * @return
     */
    public String createChecksumAsHex (String hash, int bits){
        Objects.requireNonNull(hash, "hash can't be null");

        int hexValue = bits / 4;
        StringBuilder sb = new StringBuilder(hash);
        return sb.substring(0, hexValue);
    }

    /**
     *
     * @param hash As a byte array.
     * @param bits How much of the hash that is used. Must be a multiple of 8.
     * @return
     */
    public byte[] createChecksumAsByteArray (byte[] hash, int bits){
        Objects.requireNonNull(hash, "hash can't be null");

        int useBytes = bits / 8;
        byte[] croppedArray = new byte[useBytes];

        System.arraycopy(hash, 0, croppedArray, 0, useBytes);

        return croppedArray;
    }
}