package services.datapackage;

/**
 * Created by Michael on 2016-02-12.
 */
public interface DatapackageGenerator {

    String generateDatapackage(String message);

    String openDatapackage(String encryptedMessage);
}
