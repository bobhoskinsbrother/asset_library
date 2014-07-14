package uk.co.itstherules.model;

import org.junit.Test;
import uk.co.itstherules.storage.DataStore;
import uk.co.itstherules.storage.FileDataStore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.itstherules.FileTestHelper.deleteContentsOf;
import static uk.co.itstherules.storage.DataStore.Section.Asset;

public final class ObjectRepositoryTest {

    private String path;

    public void setup() {
        path = System.getProperty("java.io.tmpdir") + "/" + "test_dir";
        final File rootDir = new File(path);
        deleteContentsOf(rootDir);
    }

    @Test public void canWriteAsset() {
        final MemDataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieve(Asset, "uuid"), is("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}"));
    }

    @Test public void canWriteAssetToFile() {
        setup();
        final DataStore store = new FileDataStore(path);
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieve(Asset, "uuid"), is("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}"));
        assertThat(new File(path).exists(), is(true));
    }

    @Test(expected = IllegalArgumentException.class) public void cannotWriteAssetWithoutUuid() {
        final MemDataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("fred", "name", "serial", "notes") {
            @Override public String getUuid() {
                return null;
            }
        };
        unit.add(asset);
    }

    @Test public void picksUpExistingRepoAndAddsItOnBootup() throws Exception {
        final DataStore store = new MemDataStore();
        store.store(Asset, "some_uuid", "{\"uuid\":\"some_uuid\",\"name\":\"a_name\",\"serialNumber\":\"a_serial\",\"notes\":\"a_notes\"}");
        store.store(Asset, "some_other_uuid", "{\"uuid\":\"some_other_uuid\",\"name\":\"b_name\",\"serialNumber\":\"b_serial\",\"notes\":\"b_notes\"}");
        ObjectRepository unit = new ObjectRepository(store);
        final Asset assetOne = unit.getAsset("some_uuid");
        assertThat(assetOne.getName(), is("a_name"));
        assertThat(assetOne.getNotes(), is("a_notes"));
        assertThat(assetOne.getSerialNumber(), is("a_serial"));
        final Asset assetTwo = unit.getAsset("some_other_uuid");
        assertThat(assetTwo.getName(), is("b_name"));
        assertThat(assetTwo.getNotes(), is("b_notes"));
        assertThat(assetTwo.getSerialNumber(), is("b_serial"));
    }

    private class MemDataStore implements DataStore {

        private final Map<Section, Map<String, String>> memory;

        private MemDataStore() {
            memory = new HashMap<>();
            memory.put(Asset, new HashMap<String, String>());
        }

        @Override public void store(Section section, String uuid, String document) {
            memory.get(section).put(uuid, document);
        }

        @Override public String retrieve(Section section, String uuid) {
            return memory.get(section).get(uuid);
        }

        @Override public Map<String, String> retrieveAll(Section section) {
            return new HashMap<String, String>(memory.get(section));
        }
    }
}
