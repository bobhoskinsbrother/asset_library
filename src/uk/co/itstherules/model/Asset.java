package uk.co.itstherules.model;

import java.util.UUID;

public class Asset implements Identifiable {

    private String uuid;
    private final String name;
    private final String serialNumber;
    private final String notes;

    public Asset(String uuid, String name, String serialNumber, String notes) {
        Check.that().isNotNull(uuid).isNotNull(name).isNotNull(serialNumber).isNotNull(notes);
        this.uuid = uuid;
        this.name = name;
        this.serialNumber = serialNumber;
        this.notes = notes;
    }

    @Override public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getNotes() {
        return notes;
    }

    @Override public boolean uuidIsPresent() {
        return uuid != null;
    }

    @Override public void generateUuid() {
        uuid = UUID.randomUUID().toString();
    }
}
