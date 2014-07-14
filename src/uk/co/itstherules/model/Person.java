package uk.co.itstherules.model;

import java.util.UUID;

public class Person implements Identifiable {

    private String uuid;
    private final String firstName;
    private final String lastName;

    public Person(String uuid, String firstName, String lastName) {
        Check.that().isNotNull(uuid).isNotNull(firstName).isNotNull(lastName);
        this.uuid = uuid;
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
        return uuid != null && !"".equals(uuid);
    }

    public void generateUuid() {
        uuid = UUID.randomUUID().toString();
    }

}
