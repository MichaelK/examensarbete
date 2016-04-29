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

    byte[] symmetricKey = "12345678123456781234567812345678".getBytes();

    CompressionGenerator compressionGenerator;
    EncryptionGenerator encryptionGenerator;
    ChecksumGenerator checksumGenerator;
    HashGenerator hashGenerator;

    public DatapackageGeneratorImpl(){
        this.compressionGenerator = new CompressionGeneratorImpl();
        this.encryptionGenerator = new EncryptionGeneratorImpl();
        this.checksumGenerator = new ChecksumGeneratorImpl();
        this.hashGenerator = new HashGeneratorImpl();
    }

    @Override
    public String generateDatapackage(String message){

        byte[] msg = message.getBytes();

        byte[] checksumHash = hashGenerator.generate(msg);

        byte[] checksum = checksumGenerator.createChecksumAsByteArray(checksumHash, 128);

        byte[] totalPackage = new byte[msg.length + checksum.length];

        System.arraycopy(msg, 0, totalPackage, 0, msg.length);

        System.arraycopy(checksum, 0, totalPackage, msg.length, checksum.length);

        byte[] compressedMsg = compressionGenerator.compress(totalPackage);

        byte[] encryptedMsg = encryptionGenerator.encrypt(compressedMsg, symmetricKey);

        return Base64.toBase64String(encryptedMsg);
    }

    @Override
    public String openDatapackage(String encryptedMessage){

        byte[] msg = Base64.decode(encryptedMessage);

        byte[] decryptedMsg = encryptionGenerator.decrypt(msg, symmetricKey);

        byte[] decompressedMsg = compressionGenerator.decompress(decryptedMsg);

        byte[] messagePart = new byte[decompressedMsg.length - 16];

        System.arraycopy(decompressedMsg, 0, messagePart, 0, messagePart.length);

        byte[] validateChecksum = new byte[16];

        System.arraycopy(decompressedMsg, messagePart.length, validateChecksum, 0, validateChecksum.length);


        byte[] checksumHash = hashGenerator.generate(messagePart);
        byte[] checksum = checksumGenerator.createChecksumAsByteArray(checksumHash, 128);
        if (!Arrays.equals(checksum, validateChecksum)){
            return "Message has been corrupted, checksum does not match!";
        }

        return new String(messagePart);
    }

//    public static void main(String[] args) {
//        DatapackageGeneratorImpl datapackageGenerator = new DatapackageGeneratorImpl();
//        String generate = datapackageGenerator.generateDatapackage("Räksmörgås!");
//        System.out.println(generate);
//        String open = datapackageGenerator.openDatapackage(generate);
//        System.out.println(open);
//
//    }
}
