package uk.co.itstherules.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class DeserializerTest {

    @Test public void canDeserializeAssetWithoutUuid() {
        final Asset deserialized = Deserializer.deserializeAsset("{\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}");
        assertThat(deserialized.getUuid(), is(notNullValue()));
        assertThat(deserialized.getName(), is("name"));
        assertThat(deserialized.getSerialNumber(), is("serial"));
        assertThat(deserialized.getNotes(), is("notes"));
    }

    @Test public void canDeserializeAssetWithUuid() {
        final Asset deserialized = Deserializer.deserializeAsset("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}");
        assertThat(deserialized.getUuid(), is("uuid"));
        assertThat(deserialized.getName(), is("name"));
        assertThat(deserialized.getSerialNumber(), is("serial"));
        assertThat(deserialized.getNotes(), is("notes"));
    }

}
