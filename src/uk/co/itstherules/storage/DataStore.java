package uk.co.itstherules.storage;

import uk.co.itstherules.model.Asset;
import uk.co.itstherules.model.Person;
import uk.co.itstherules.model.ReserveAsset;

import java.awt.geom.Area;
import java.util.List;
import java.util.Map;

public interface DataStore {

    void store(Asset asset);
    void store(Person person);
    void store(ReserveAsset reserveAsset);

    void removeAsset(String uuid);
    void removePerson(String uuid);
    void removeReserveAsset(String uuid);

    Asset retrieveAsset(String uuid);
    Person retrievePerson(String uuid);
    ReserveAsset retrieveReserveAsset(String uuid);

    List<Person> retrieveAllPeople();
    List<Asset> retrieveAllAssets();
    List<ReserveAsset>  retrieveAllReservedAssets();

}
