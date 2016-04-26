package services.json;

/**
 * Created by mickal on 08/02/2016.
 */
public interface JsonGenerator {

    /**
     * Generic Object/Class is turned into a json string format: {"dna":{"dna":123124},"oldDna":{"dna":123124},"metadata":{"data":[74,101,107]}}
     * @param t
     * @param <T> Generic Object/Class
     * @return
     */
    <T> String generate(T t);

    <T> T jsonToObject(String t, Class<T> clazz);
}