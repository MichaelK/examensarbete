package services.datapackage;

import org.bouncycastle.util.encoders.Base64;
import services.checksum.ChecksumGenerator;
import services.checksum.ChecksumGeneratorImpl;
import services.compression.CompressionGenerator;
import services.compression.CompressionGeneratorImpl;
import services.encryption.EncryptionGenerator;
import services.encryption.EncryptionGeneratorImpl;
import services.hash.HashGenerator;
import services.hash.HashGeneratorImpl;

import java.util.Arrays;

/**
 * Created by Michael on 2016-02-12.
 */
public class DatapackageGeneratorImpl implements DatapackageGenerator {

    // Below services needed to generate and open packages.
    private CompressionGenerator compressionGenerator;
    private EncryptionGenerator encryptionGenerator;
    private ChecksumGenerator checksumGenerator;
    private HashGenerator hashGenerator;

    public DatapackageGeneratorImpl(){
        this.compressionGenerator = new CompressionGeneratorImpl();
        this.encryptionGenerator = new EncryptionGeneratorImpl();
        this.checksumGenerator = new ChecksumGeneratorImpl();
        this.hashGenerator = new HashGeneratorImpl();
    }

    @Override
    public String generateDatapackage(String message, byte[] symmetricKey){
        // Take message and convert it to a byte array.
        byte[] msg = message.getBytes();
        // Create a hash by hashing the message.
        byte[] checksumHash = hashGenerator.generate(msg);
        // Create a checksum of a certain length.
        byte[] checksum = checksumGenerator.createChecksumAsByteArray(checksumHash, 128);
        // Create a new byte array to hold the complete package.
        byte[] totalPackage = new byte[msg.length + checksum.length];
        // Copy message to byte array.
        System.arraycopy(msg, 0, totalPackage, 0, msg.length);
        // Copy checksum to byte array.
        System.arraycopy(checksum, 0, totalPackage, msg.length, checksum.length);
        // Compress the complete package.
        byte[] compressedMsg = compressionGenerator.compress(totalPackage);
        // Encrypt the compressed package.
        byte[] encryptedMsg = encryptionGenerator.encrypt(compressedMsg, symmetricKey);
        // Return the package converted to Base64 format.
        return Base64.toBase64String(encryptedMsg);
    }

    @Override
    public String openDatapackage(String encryptedMessage, byte[] symmetricKey){
        // Take the package and convert if from Base64 format.
        byte[] msg = Base64.decode(encryptedMessage);
        // Decrypt the package.
        byte[] decryptedMsg = encryptionGenerator.decrypt(msg, symmetricKey);
        // Decompress the package.
        byte[] decompressedMsg = compressionGenerator.decompress(decryptedMsg);
        // Create a new byte array to hold only the message.
        byte[] messagePart = new byte[decompressedMsg.length - 16];
        // Copy the message to the array.
        System.arraycopy(decompressedMsg, 0, messagePart, 0, messagePart.length);
        // Create a new byte array to hold the checksum.
        byte[] validateChecksum = new byte[16];
        // Copy the checksum part of the package to the checksum array.
        System.arraycopy(decompressedMsg, messagePart.length, validateChecksum, 0, validateChecksum.length);
        // Generate a new checksum hash with the extracted message.
        byte[] checksumHash = hashGenerator.generate(messagePart);

        // Crop the checksum hash into the 16 bytes that is the checksum.
        byte[] checksum = checksumGenerator.createChecksumAsByteArray(checksumHash, 128);
        // Validate that the checksums match.
        if (!Arrays.equals(checksum, validateChecksum)){
            return "Message has been corrupted, checksum does not match!";
        }
        // Return the message.
        return new String(messagePart);
    }
}
