package uk.co.itstherules.model;

import org.junit.Test;
import uk.co.itstherules.storage.DataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public final class ObjectRepositoryTest {

    @Test public void canDestroyAsset() {
        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieveAsset("uuid").getSerialNumber(), is("serial"));

        unit.destroyAsset("uuid");
        assertThat(store.retrieveAsset("uuid"), nullValue());
    }

    @Test public void canDestroyReserveAsset() {
        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final ReserveAsset asset = new ReserveAsset("uuid", "personUuid","assetUuid");
        unit.add(asset);
        assertThat(store.retrieveReserveAsset("uuid").getPersonUuid(), is("personUuid"));

        unit.destroyReserveAsset("uuid");
        assertThat(store.retrieveReserveAsset("uuid"), nullValue());
    }

    @Test public void cannotDestroyReserveAssetWithBadId() {
        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final ReserveAsset asset = new ReserveAsset("uuid", "personUuid","assetUuid");
        unit.add(asset);
        assertThat(store.retrieveReserveAsset("uuid").getPersonUuid(), is("personUuid"));

        unit.destroyReserveAsset("fred");
        assertThat(store.retrieveReserveAsset("uuid").getPersonUuid(), is("personUuid"));
    }

    @Test public void canDestroyReserveAssetByAssetUuid() {
        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final ReserveAsset asset = new ReserveAsset("uuid", "personUuid","assetUuid");
        unit.add(asset);
        assertThat(store.retrieveReserveAsset("uuid").getPersonUuid(), is("personUuid"));

        unit.destroyReserveAssetByAssetId("assetUuid");
        assertThat(store.retrieveReserveAsset("uuid"), nullValue());
    }
    @Test public void cannotDestroyReserveAssetUsingBadAssetUuid() {

        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final ReserveAsset asset = new ReserveAsset("uuid", "personUuid","assetUuid");
        unit.add(asset);
        assertThat(store.retrieveReserveAsset("uuid").getPersonUuid(), is("personUuid"));

        unit.destroyReserveAssetByAssetId("fred");
        assertThat(store.retrieveReserveAsset("uuid").getAssetUuid(), is("assetUuid"));
    }

    @Test
    public void canWriteAsset() {
        DataStore store = new MemDataStore();
        ObjectRepository unit = new ObjectRepository(store);
        final Asset asset = new Asset("uuid", "name", "serial", "notes");
        unit.add(asset);
        assertThat(store.retrieveAsset("uuid").getSerialNumber(), is("serial"));
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
        store.store(new Asset("some_uuid", "a_name","a_serial","a_notes"));
        store.store(new Asset("some_other_uuid", "b_name","b_serial","b_notes"));
        store.store(new Person("yet_another_uuid","Fred","Fredoferson"));
        store.store(new ReserveAsset("yet_again_another_uuid", "yet_another_uuid","some_other_uuid"));

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

        private final Map<String, Asset> assets;
        private final Map<String, Person> people;
        private final Map<String, ReserveAsset> reservedAssets;

        private MemDataStore() {
            assets = new HashMap<>();
            people = new HashMap<>();
            reservedAssets = new HashMap<>();
        }

        @Override  public void store(Asset asset) {
            assets.put(asset.getUuid(), asset);
        }
        @Override  public void store(Person person) {
            people.put(person.getUuid(), person);
        }
        @Override  public void store(ReserveAsset reserveAsset) {
            reservedAssets.put(reserveAsset.getUuid(), reserveAsset);
        }

        @Override
        public void removeAsset(String area) {
            assets.remove(area);
        }

        @Override
        public void removePerson(String uuid) {
            people.remove(uuid);
        }

        @Override
        public void removeReserveAsset(String uuid) {
            reservedAssets.remove(uuid);
        }

        @Override
        public Asset retrieveAsset(String uuid) {
            return assets.get(uuid);
        }

        @Override
        public Person retrievePerson(String uuid) {
            return people.get(uuid);
        }

        @Override
        public ReserveAsset retrieveReserveAsset(String uuid) {
            return reservedAssets.get(uuid);
        }

        @Override
        public List<Person> retrieveAllPeople() {
            return new ArrayList<>(people.values());
        }

        @Override
        public List<Asset> retrieveAllAssets() {
            return new ArrayList<>(assets.values());
        }

        @Override
        public List<ReserveAsset> retrieveAllReservedAssets() {
            return new ArrayList<>(reservedAssets.values());
        }
    }
}
