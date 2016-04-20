package uk.co.itstherules.storage;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollectionsConverter {

    private CollectionsConverter(){}

    public static Map<String, String> map(String... values) {
        if((values.length%2) != 0) {
            throw new IllegalArgumentException("Requires even number of arguments");
        }
        Map<String, String> reply = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i+=2) {
            String key = values[i];
            String value = values[i+1];
            reply.put(key,value);
        }
        return reply;
    }

    public static String string(String[] values, String delimiter) {
        Iterator<String> iterator = Arrays.asList(values).iterator();
        StringBuilder b = new StringBuilder();
        while(iterator.hasNext()) {
            b.append(iterator.next());
            if(iterator.hasNext()) {
                b.append(delimiter);
            }
        }
        return b.toString();
    }

}
