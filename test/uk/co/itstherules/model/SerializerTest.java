package uk.co.itstherules.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class SerializerTest {

    @Test public void canSerializeAsset() {
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        assertThat(Serializer.serialize(asset), is("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}"));
    }

    @Test public void canSerializeAssets() {
        final Assets assets = new Assets();
        final Asset asset1 = new Asset("uuid1", "name1", "serial1", "notes1");
        final Asset asset2 = new Asset("uuid2", "name2", "serial2", "notes2");
        assets.add(asset1).add(asset2);
        assertThat(Serializer.serialize(assets), is(
                "{\"assets\":" +
                        "{" +
                            "\"uuid2\":{\"uuid\":\"uuid2\",\"name\":\"name2\",\"serialNumber\":\"serial2\",\"notes\":\"notes2\"}," +
                            "\"uuid1\":{\"uuid\":\"uuid1\",\"name\":\"name1\",\"serialNumber\":\"serial1\",\"notes\":\"notes1\"}" +
                        "}" +
                "}"));
    }

}
