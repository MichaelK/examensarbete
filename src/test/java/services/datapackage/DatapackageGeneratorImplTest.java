package services.datapackage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Michael on 2016-05-07.
 */
public class DatapackageGeneratorImplTest {

    private DatapackageGenerator datapackageGenerator;

    @Before
    public void init(){
        datapackageGenerator = new DatapackageGeneratorImpl();
    }

    @Test
    public void generateAndOpenPackageTest(){
        // Random test string.
        String testString = "This is a test string to test with! It even has swedish chars like å, Ä and ö.";
        // Create a symmetric key.
        byte[] symmetricKey = "12345678123456781234567812345678".getBytes();
        // Generate a package.
        String generatedPaackage = datapackageGenerator.generateDatapackage(testString, symmetricKey);
        // Open the package.
        String openedPackage = datapackageGenerator.openDatapackage(generatedPaackage, symmetricKey);
        // Assert message same.
        Assert.assertEquals(openedPackage, testString);
    }

    @Test
    public void validateChecksumTest(){
        // Datapackage with bad checksum.
        String badChecksumString = "1u2k1DD+8arh9eTwL0EoCdJedxDi8p++f3zEvVMljMu50AR/wQiHqjWWv5vPdDzTtZTArgRhswqf5c5Mx/YZxjEdesCE5fa6PrmmV3kAN4JYgzwRcswNLnpYu2R2tU7i";
        // Create a symmetric key.
        byte[] symmetricKey = "12345678123456781234567812345678".getBytes();
        // Open the datapackage.
        String openPackageWithBadChecksum = datapackageGenerator.openDatapackage(badChecksumString, symmetricKey);
        // The expected message if checksum does not match.
        String expected = "Message has been corrupted, checksum does not match!";
        // Assert.
        Assert.assertEquals(openPackageWithBadChecksum, expected );
    }
}
