package uk.co.itstherules.model;

import com.google.gson.Gson;

public final class Serializer {

    public static String serialize(People people) {
        return serializeObject(people);
    }

    public static String serialize(Person person) {
        return serializeObject(person);
    }

    public static String serialize(ReserveAsset reserveAsset) {
        return serializeObject(reserveAsset);
    }
    public static String serialize(Asset asset) {
        return serializeObject(asset);
    }

    public static String serialize(Assets assets) {
        return serializeObject(assets);
    }

    private static String serializeObject(Object object) {
        final Gson gson = new Gson();
        return gson.toJson(object);
    }
}
