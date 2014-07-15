package uk.co.itstherules.storage;

import java.util.Map;

public interface DataStore {


    enum Section {
        Person, Asset, ReserveAsset
    }
    void store(Section section, String uuid, String document);
    void remove(Section section, String uuid);
    String retrieve(Section section, String uuid);
    Map<String,String> retrieveAll(Section section);

}
