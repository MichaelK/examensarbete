package services.compression;

/**
 * Created by mickal on 05/02/2016.
 */
public interface CompressionGenerator {

    /**
     * Compress a byte array.
     * @param data
     * @return
     */
    byte[] compress(byte[] data);

    /**
     * Decompress a byte array.
     * @param data
     * @return
     */
    byte[] decompress(byte[] data);
}