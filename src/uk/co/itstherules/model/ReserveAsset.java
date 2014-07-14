package uk.co.itstherules.model;

import java.util.UUID;

public class ReserveAsset implements Identifiable {

    private String uuid;
    private String personUuid;
    private String assetUuid;

    public ReserveAsset(String uuid, String personUuid, String assetUuid) {
        this.uuid = uuid;
        Check.that().isNotNull(personUuid).isNotNull(assetUuid);
        this.personUuid = personUuid;
        this.assetUuid = assetUuid;
    }

    @Override public String getUuid() {
        return uuid;
    }

    public String getPersonUuid() {
        return personUuid;
    }

    public String getAssetUuid() {
        return assetUuid;
    }

    @Override public boolean uuidIsPresent() {
        return uuid != null;
    }

    @Override public void generateUuid() {
        uuid = UUID.randomUUID().toString();
    }
}
