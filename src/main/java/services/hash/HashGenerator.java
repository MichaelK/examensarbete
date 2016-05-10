package services.hash;

/**
 * Created by mickal on 02/02/2016.
 */
public interface HashGenerator {

    /**
     * Uses default Sha3-512.
     * @param string
     * @return
     */
    byte[] generate(final String string);

    /**
     * Uses default Sha3-512.
     * @param bytes
     * @return
     */
    byte[] generate(final byte[] bytes);

    /**
     * Can chose between Sha3: 224 bit, 256 bit, 384 bit and 512 bit.
     * @param string
     * @param bits
     * @return
     */
    byte[] generate(final String string, final int bits);

    /**
     * Can chose between Sha3: 224 bit, 256 bit, 384 bit and 512 bit.
     * @param bytes
     * @param bits
     * @return
     */
    byte[] generate(final byte[] bytes, final int bits);

    /**
     * Makes a Sha3-512 bit hash but only returns a part of it with the length "bits".
     * @param bytes
     * @param bits
     * @return
     */
    byte[] hashToBits(final byte[] bytes, final int bits);
}