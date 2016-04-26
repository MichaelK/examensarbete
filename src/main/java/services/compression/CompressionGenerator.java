package services.compression;

/**
 * Created by mickal on 05/02/2016.
 */
public interface CompressionGenerator {

    /**
     *
     * @param data
     * @return
     */
    byte[] compress(byte[] data);

    /**
     *
     * @param data
     * @return
     */
    byte[] decompress(byte[] data);
}