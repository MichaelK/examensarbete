package services.compression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Michael on 2016-05-07.
 */
public class CompressionGeneratorImplTest {

    private CompressionGenerator compressionGenerator;

    @Before
    public void init(){
        compressionGenerator = new CompressionGeneratorImpl();
    }

    @Test
    public void deflateAndInflateTest(){
        // Random test string.
        String testString = "This is a test string to test with! It even has swedish chars like å, Ä and ö.";
        // Convert test string into byte array.
        byte[] bytes = testString.getBytes();
        // Compress the byte array with the test method.
        byte[] compressed = compressionGenerator.compress(bytes);
        // Decompress the compressed byte array.
        byte[] decompressed = compressionGenerator.decompress(compressed);
        // Assert that the original byte array is the same.
        Assert.assertArrayEquals(bytes, decompressed);
    }
}
