package services.checksum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Michael on 2016-05-07.
 */
public class ChecksumGeneratorImplTest {

    // Class to test.
    private ChecksumGenerator checksumGenerator;
    // Static hex string to use for test.
    private String HEXSTRING = "50f5636d6573a9514a5efdb28b964794b7ffd75660a5b66b89";
    // Static byte array to use for test.
    private byte[] BYTEARRAY = HEXSTRING.getBytes();

    @Before
    public void init(){
        checksumGenerator = new ChecksumGeneratorImpl();
    }

    @Test
    public void createChecksumAsHexTest(){
        // Number of bits to use.
        int numberOfBits = 64;
        // Expected string
        String hexExpected = "50f5636d6573a951";
        // Test the method.
        String hexActual = checksumGenerator.createChecksumAsHex(HEXSTRING, numberOfBits);
        // Assert outcome same as expected.
        Assert.assertEquals(hexExpected, hexActual);
    }

    @Test(expected = NullPointerException.class)
    public void createChecksumAsHexWithNullHashAsInputTest(){
        // Number of bits to use.
        int numberOfBits = 64;
        // Hex used is null.
        String nullHex = null;
        // Test the method. Should throw NullPointerException.
        String hexActual = checksumGenerator.createChecksumAsHex(nullHex, numberOfBits);
    }

    @Test
    public void createChecksumAsByteArrayTest(){
        // Number of bits to use.
        int numberOfBits = 128;
        // Expected byte array.
        byte[] byteArrayExpected = "50f5636d6573a951".getBytes();
        // Test the method.
        byte[] byteArrayActual = checksumGenerator.createChecksumAsByteArray(BYTEARRAY, numberOfBits);
        // Assert outcome same as expected.
        Assert.assertArrayEquals(byteArrayExpected, byteArrayActual);
    }

    @Test(expected = NullPointerException.class)
    public void createChecksumAsByteArrayWithNullHashAsInput(){
        // Number of bits to use.
        int numberOfBits = 128;
        // Expected byte array.
        byte[] byteArrayExpected = "50f5636d6573a951".getBytes();
        // Byte array that is null.
        byte[] nullArray = null;
        // Test the method.
        byte[] byteArrayActual = checksumGenerator.createChecksumAsByteArray(nullArray, numberOfBits);
    }

}
