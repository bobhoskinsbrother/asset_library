package uk.co.itstherules.model;

import java.util.UUID;

public final class Person implements Identifiable {

    private String uuid;
    private final String firstName;
    private final String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean uuidIsPresent() {
        return uuid != null;
    }

    public void generateUuid() {
        uuid = UUID.randomUUID().toString();
    }

}
