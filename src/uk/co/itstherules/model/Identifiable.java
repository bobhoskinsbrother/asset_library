package uk.co.itstherules.model;

public interface Identifiable {

    String getUuid();
    boolean uuidIsPresent();
    void generateUuid();
}
