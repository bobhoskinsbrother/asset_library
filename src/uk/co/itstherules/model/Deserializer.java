package uk.co.itstherules.model;

import com.google.gson.Gson;

public final class Deserializer {

    private Deserializer(){}

    public static ReserveAsset deserializeReserveAsset(String s) {
        return deserialize(s, ReserveAsset.class);
    }

    public static Asset deserializeAsset(String s) {
        return deserialize(s, Asset.class);
    }

    public static Person deserializePerson(String s) {
        return deserialize(s, Person.class);
    }

    private static <T extends Identifiable> T deserialize(String s, Class<T> theClass) {
        final Gson gson = new Gson();
        final T reply = gson.fromJson(s, theClass);
        if (!reply.uuidIsPresent()) {
            reply.generateUuid();
        }
        return reply;
    }

}