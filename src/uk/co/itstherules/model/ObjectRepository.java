package uk.co.itstherules.model;

import uk.co.itstherules.storage.DataStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        final List<ReserveAsset> documents = store.retrieveAllReservedAssets();
        for (ReserveAsset reserveAsset : documents) {
            reserveAssets.put(reserveAsset.getUuid(), reserveAsset);
        }
    }

    private void addAssetsFrom(DataStore store) {
        final List<Asset> documents = store.retrieveAllAssets();
        for (Asset asset : documents) {
            assets.add(asset);
        }
    }

    private void addPeopleFrom(DataStore store) {
        final List<Person> documents = store.retrieveAllPeople();
        for (Person person : documents) {
            people.add(person);
        }
    }

    public Asset getAsset(String uuid) throws NotFoundException {
        return assets.get(uuid);
    }
    public ReserveAsset getReserveAsset(String uuid) throws NotFoundException {
        uk.co.itstherules.model.ReserveAsset reserveAsset = reserveAssets.get(uuid);
        if(reserveAsset == null) {
            throw new NotFoundException();
        }
        return reserveAsset;
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
        store.store(asset);
        assets.add(asset);
    }

    public void add(Person person) {
        final String uuid = person.getUuid();
        Check.that().isNotNull(uuid);
        store.store(person);
        people.add(person);
    }

    public void add(ReserveAsset reserveAsset) {
        final String uuid = reserveAsset.getUuid();
        Check.that().isNotNull(uuid);
        store.store(reserveAsset);
        reserveAssets.put(uuid, reserveAsset);
    }

    public boolean isAssetAvailable(String assetUuid) {
        Check.that().isNotNull(assetUuid);
        if(!assets.contains(assetUuid)) return false;
        final Collection<ReserveAsset> values = reserveAssets.values();
        for (ReserveAsset value : values) {
            if(assetUuid.equals(value.getAssetUuid())) {
                return false;
            }
        }
        return true;
    }

    public uk.co.itstherules.model.Person personWhoReservedAsset(String assetUuid) throws NotFoundException {
        Check.that().isNotNull(assetUuid);
        final Collection<ReserveAsset> values = reserveAssets.values();
        for (ReserveAsset reserveAsset : values) {
            if(assetUuid.equals(reserveAsset.getAssetUuid())) {
                return people.get(reserveAsset.getPersonUuid());
            }
        }
        throw new IllegalArgumentException();
    }

    public Collection<ReserveAsset> allReserveAssets() {
        return reserveAssets.values();
    }

    public void destroyReserveAsset(String uuid) {
        reserveAssets.remove(uuid);
        store.removeReserveAsset(uuid);
    }

    public void destroyAsset(String uuid) {
        assets.remove(uuid);
        store.removeAsset(uuid);
    }

    public void destroyReserveAssetByAssetId(String assetUuid) {
        List<String> toDelete = new ArrayList<>();
        for (ReserveAsset reserveAsset : reserveAssets.values()) {
            if(assetUuid.equals(reserveAsset.getAssetUuid())){
                toDelete.add(reserveAsset.getUuid());
            }
        }
        for (String uuid : toDelete) {
            destroyReserveAsset(uuid);
        }
    }
}
