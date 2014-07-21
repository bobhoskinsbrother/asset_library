package uk.co.itstherules.model;

interface Identifiable {

    String getUuid();
    boolean uuidIsPresent();
    void generateUuid();
}
