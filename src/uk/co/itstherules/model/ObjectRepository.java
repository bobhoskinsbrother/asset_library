package uk.co.itstherules.model;

import uk.co.itstherules.storage.DataStore;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static uk.co.itstherules.storage.DataStore.Section.*;

public final class ObjectRepository {

    private final Assets assets;
    private final People people;
    private final Map<String, ReserveAsset> reserveAssets;
    private final DataStore store;

    public ObjectRepository(DataStore store) {
        this.store = store;
        reserveAssets = new ConcurrentHashMap<>();
        assets = new Assets();
        people = new People();
        addReserveAssetsFrom(store);
        addAssetsFrom(store);
        addPeopleFrom(store);
    }

    private void addReserveAssetsFrom(DataStore store) {
        final Map<String, String> documents = store.retrieveAll(ReserveAsset);
        final Set<String> keys = documents.keySet();
        for (String key : keys) {
            String document = documents.get(key);
            final uk.co.itstherules.model.ReserveAsset reserveAsset = Deserializer.deserializeReserveAsset(document);
            reserveAssets.put(reserveAsset.getUuid(), reserveAsset);
        }
    }

    private void addAssetsFrom(DataStore store) {
        final Map<String, String> documents = store.retrieveAll(Asset);
        final Set<String> keys = documents.keySet();
        for (String key : keys) {
            String document = documents.get(key);
            final Asset deserialized = Deserializer.deserializeAsset(document);
            assets.add(deserialized);
        }
    }

    private void addPeopleFrom(DataStore store) {
        final Map<String, String> documents = store.retrieveAll(Person);
        final Set<String> keys = documents.keySet();
        for (String key : keys) {
            String document = documents.get(key);
            final Person deserialized = Deserializer.deserializePerson(document);
            people.add(deserialized);
        }
    }

    public Asset getAsset(String uuid) throws NotFoundException {
        return assets.get(uuid);
    }

    public Person getPerson(String uuid) {
        return people.get(uuid);
    }

    public Assets allAssets() {
        return assets;
    }

    public People allPeople() {
        return people;
    }

    public void add(Asset asset) {
        final String uuid = asset.getUuid();
        Check.that().isNotNull(uuid);
        store.store(Asset, uuid, Serializer.serialize(asset));
        assets.add(asset);
    }

    public void add(Person person) {
        final String uuid = person.getUuid();
        Check.that().isNotNull(uuid);
        store.store(Person, uuid, Serializer.serialize(person));
        people.add(person);
    }

    public void add(ReserveAsset reserveAsset) {
        final String uuid = reserveAsset.getUuid();
        Check.that().isNotNull(uuid);
        store.store(ReserveAsset, uuid, Serializer.serialize(reserveAsset));
        reserveAssets.put(uuid, reserveAsset);
    }

    public boolean isAssetAvailable(String assetUuid) {
        Check.that().isNotNull(assetUuid);
        final Collection<ReserveAsset> values = reserveAssets.values();
        for (ReserveAsset value : values) {
            if(assetUuid.equals(value.getAssetUuid())) {
                return false;
            }
        }
        return true;
    }

    public uk.co.itstherules.model.Person personWhoReservedAsset(String assetUuid) {
        Check.that().isNotNull(assetUuid);
        final Collection<ReserveAsset> values = reserveAssets.values();
        for (ReserveAsset reserveAsset : values) {
            if(assetUuid.equals(reserveAsset.getAssetUuid())) {
                return people.get(reserveAsset.getPersonUuid());
            }
        }
        throw new IllegalArgumentException();
    }
}
