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
import static uk.co.itstherules.storage.DataStore.Section.Person;
import static uk.co.itstherules.storage.DataStore.Section.ReserveAsset;

public final class ObjectRepositoryTest {

    private String path;

    public void setup() {
        path = System.getProperty("java.io.tmpdir") + "/" + "test_dir";
        final File rootDir = new File(path);
        deleteContentsOf(rootDir);
    }

    @Test
    public void canWriteAsset() {
        final MemDataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieve(Asset, "uuid"), is("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}"));
    }

    @Test
    public void canWriteAssetToFile() {
        setup();
        final DataStore store = new FileDataStore(path);
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieve(Asset, "uuid"), is("{\"uuid\":\"uuid\",\"name\":\"name\",\"serialNumber\":\"serial\",\"notes\":\"notes\"}"));
        assertThat(new File(path).exists(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotWriteAssetWithoutUuid() {
        final MemDataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("fred", "name", "serial", "notes") {
            @Override
            public String getUuid() {
                return null;
            }
        };
        unit.add(asset);
    }

    @Test
    public void picksUpExistingRepoAndAddsItOnBootup() throws Exception {
        final DataStore store = new MemDataStore();
        store.store(Asset, "some_uuid", "{\"uuid\":\"some_uuid\",\"name\":\"a_name\",\"serialNumber\":\"a_serial\",\"notes\":\"a_notes\"}");
        store.store(Asset, "some_other_uuid", "{\"uuid\":\"some_other_uuid\",\"name\":\"b_name\",\"serialNumber\":\"b_serial\",\"notes\":\"b_notes\"}");
        store.store(Person, "yet_another_uuid", "{\"uuid\":\"yet_another_uuid\",\"firstName\":\"Fred\",\"lastName\":\"Fredoferson\"}");
        store.store(ReserveAsset, "yet_again_another_uuid", "{\"uuid\":\"yet_again_another_uuid\",\"personUuid\":\"yet_another_uuid\",\"assetUuid\":\"some_other_uuid\"}");

        ObjectRepository unit = new ObjectRepository(store);
        final Asset assetOne = unit.getAsset("some_uuid");
        assertThat(assetOne.getName(), is("a_name"));
        assertThat(assetOne.getNotes(), is("a_notes"));
        assertThat(assetOne.getSerialNumber(), is("a_serial"));

        final Asset assetTwo = unit.getAsset("some_other_uuid");
        assertThat(assetTwo.getName(), is("b_name"));
        assertThat(assetTwo.getNotes(), is("b_notes"));
        assertThat(assetTwo.getSerialNumber(), is("b_serial"));

        final Person person = unit.getPerson("yet_another_uuid");
        assertThat(person.getFirstName(), is("Fred"));
        assertThat(person.getLastName(), is("Fredoferson"));

        uk.co.itstherules.model.ReserveAsset reserveAsset = unit.getReserveAsset("yet_again_another_uuid");
        assertThat(reserveAsset.getAssetUuid(), is("some_other_uuid"));
        assertThat(reserveAsset.getPersonUuid(), is("yet_another_uuid"));

    }

    @Test(expected = NotFoundException.class)
    public void cannotFindAsset() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.getAsset("not_present");
    }

    @Test(expected = NotFoundException.class)
    public void cannotFindPerson() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.getPerson("not_present");
    }

    @Test(expected = NotFoundException.class)
    public void cannotFindReserveAsset() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.getReserveAsset("not_present");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddingPersonWithoutUuid() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.add(new Person("", "", "") {
            @Override
            public String getUuid() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddingAssetWithoutUuid() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.add(new Asset("", "", "", "") {
            @Override
            public String getUuid() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenAddingReserveAssetWithoutUuid() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.add(new ReserveAsset(null, "", ""));
    }

    @Test
    public void canAddPerson() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        uk.co.itstherules.model.Person person = new Person("uuid", "Ben", "Benn");
        person.generateUuid();
        unit.add(person);
    }

    @Test
    public void canAddAsset() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        uk.co.itstherules.model.Asset input = new Asset("uuid", "name", "serial", "notes");
        unit.add(input);
        uk.co.itstherules.model.Asset reply = unit.getAsset("uuid");
        assertThat(reply, is(input));
    }

    @Test
    public void canAddReserveAsset() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        uk.co.itstherules.model.ReserveAsset input = new ReserveAsset("uuid", "pUuid", "aUuid");
        unit.add(input);
        uk.co.itstherules.model.ReserveAsset reply = unit.getReserveAsset("uuid");
        assertThat(input, is(reply));
    }

    @Test
    public void assetIsAvailableWhenInStock() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(unit.isAssetAvailable("uuid"), is(true));
    }

    @Test
    public void assetIsNotAvailableWhenNotInStock() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        assertThat(unit.isAssetAvailable("uuid"), is(false));
    }

    @Test
    public void assetIsNotAvailableWhenReserved() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        Asset asset = new Asset("aUuid", "name", "serial", "notes");
        unit.add(asset);
        ReserveAsset reserveAsset = new ReserveAsset("ruuid", "pUuid", "aUuid");
        unit.add(reserveAsset);
        assertThat(unit.isAssetAvailable("aUuid"), is(false));
    }

    @Test
    public void getPersonWhoReservedAsset() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        Asset asset = new Asset("aUuid", "name", "serial", "notes");
        unit.add(asset);
        ReserveAsset reserveAsset = new ReserveAsset("ruuid", "pUuid", "aUuid");
        unit.add(reserveAsset);
        Person person = new Person("pUuid", "first", "last");
        unit.add(person);
        assertThat(unit.personWhoReservedAsset("aUuid"), is(person));
    }

    @Test(expected = NotFoundException.class)
    public void whenPersonIsNotThereThrowsNotFound() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        Asset asset = new Asset("aUuid", "name", "serial", "notes");
        unit.add(asset);
        ReserveAsset reserveAsset = new ReserveAsset("ruuid", "pUuid", "aUuid");
        unit.add(reserveAsset);
        unit.personWhoReservedAsset("aUuid");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenNullValue() {
        ObjectRepository unit = new ObjectRepository(new MemDataStore());
        unit.personWhoReservedAsset(null);
    }

    private class MemDataStore implements DataStore {

        private final Map<Section, Map<String, String>> memory;

        private MemDataStore() {
            memory = new HashMap<>();
            memory.put(Asset, new HashMap<String, String>());
            memory.put(ReserveAsset, new HashMap<String, String>());
            memory.put(Person, new HashMap<String, String>());
        }

        @Override
        public void store(Section section, String uuid, String document) {
            memory.get(section).put(uuid, document);
        }

        @Override
        public void remove(Section section, String uuid) {
            memory.get(section).remove(uuid);
        }

        @Override
        public String retrieve(Section section, String uuid) {
            return memory.get(section).get(uuid);
        }

        @Override
        public Map<String, String> retrieveAll(Section section) {
            return new HashMap<>(memory.get(section));
        }
    }
}
