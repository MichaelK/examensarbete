package services.json;

import com.google.gson.Gson;

import java.io.Serializable;

public class JsonGeneratorImpl implements JsonGenerator, Serializable {

    /**
     * Generic Object/Class is turned into a json string format: {"dna":{"dna":123124},"oldDna":{"dna":123124},"metadata":{"data":[74,101,107]}}
     * @param t
     * @param <T> Generic Object/Class
     * @return
     */
    //@Override
    public <T> String generate(T t){
        Gson gson = new Gson();
        return gson.toJson(t);
    }

    //@Override
    public <T> T jsonToObject(String t, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(t, clazz);
    }
}