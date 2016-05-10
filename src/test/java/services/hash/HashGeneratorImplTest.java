package services.hash;

import org.bouncycastle.util.encoders.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Michael on 2016-05-07.
 */
public class HashGeneratorImplTest {

    private HashGenerator hashGenerator;

    // Create a testString to test with.
    private String testString = "This is a test string to test with! It even has swedish chars like å, Ä and ö.";
    // Create a byte array from the test string.
    private byte[] testStringAsByteArray = testString.getBytes();
    // This is what hashing the test string, with Sha3-512, should be after Base64 conversion.
    private String base64HashString = "G1hLNrc5OKYS8MS2qhrTD5Wz4FrfFT67oazyUDA6m0dNx3DuwOarNhegxUUZFItSuBl/AaKsUwElh0WK+zVLMQ==";
    // This is the byte array of the base64HashString
    private byte[] hashStringAsArray = base64HashString.getBytes();

    @Before
    public void init(){
        hashGenerator = new HashGeneratorImpl();
    }

    @Test
    public void generate512HashWithStringInputTest(){
        // Create a hash using the string testString.
        byte[] hash = hashGenerator.generate(testString);
        // Use Base64 conversion so get a byte array we can check.
        byte[] base64hash = Base64.encode(hash);
        // Assert same
        Assert.assertArrayEquals(base64hash, hashStringAsArray);
    }

    @Test
    public void generate512HashWithByteArrayInputTest(){
        // Create a hsah using the byte array testStringAsByteArray.
        byte[] hash = hashGenerator.generate(testStringAsByteArray);
        // Use Base64 conversion so get a byte array we can check.
        byte[] base64hash = Base64.encode(hash);
        // Assert same
        Assert.assertArrayEquals(base64hash, hashStringAsArray );
    }

    @Test
    public void hashToBitsTest(){
        // Create a 512 hash the normal way.
        byte[] hash512 = hashGenerator.generate(testStringAsByteArray, 512);
        // Create an empty byte array. 16 bytes = 128 bits.
        byte[] testArray = new byte[16];
        // Copy the first 16 bytes to the testArray.
        System.arraycopy(hash512, 0, testArray, 0, 16);

        // Create a hash that will be cropped by hashToBits.
        byte[] hashToBits128 = hashGenerator.hashToBits(testStringAsByteArray, 128);

        //Assert that they are the same.
        Assert.assertArrayEquals(hashToBits128, testArray);
    }
}
