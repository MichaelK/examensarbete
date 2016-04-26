package services.datapackage;

import org.bouncycastle.util.encoders.Base64;
import services.compression.CompressionGeneratorImpl;
import services.encryption.EncryptionGeneratorImpl;

/**
 * Created by Michael on 2016-02-12.
 */
public class DatapackageGeneratorImpl implements DatapackageGenerator {

    byte[] symmetricKey = "12345678123456781234567812345678".getBytes();

    CompressionGeneratorImpl compressionGenerator;
    EncryptionGeneratorImpl encryptionGenerator;

    public DatapackageGeneratorImpl(){
        this.compressionGenerator = new CompressionGeneratorImpl();
        this.encryptionGenerator = new EncryptionGeneratorImpl();
    }

    @Override
    public String generateDatapackage(String message){

        byte[] msg = message.getBytes();

        byte[] compressedMsg = compressionGenerator.compress(msg);

        byte[] encryptedMsg = encryptionGenerator.encrypt(compressedMsg, symmetricKey);

        return Base64.toBase64String(encryptedMsg);
    }

    @Override
    public String openDatapackage(String encryptedMessage){

        byte[] msg = Base64.decode(encryptedMessage);

        byte[] decrptedMsg = encryptionGenerator.decrypt(msg, symmetricKey);

        byte[] decompressedMsg = compressionGenerator.decompress(decrptedMsg);

        return new String(decompressedMsg);
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
