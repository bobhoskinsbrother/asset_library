package uk.co.itstherules.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class People {

    private final Map<String, Person> people;

    public People() {
        this.people = new ConcurrentHashMap<>();
    }

    public People add(Person person) {
        people.put(person.getUuid(), person);
        return this;
    }

    public Person get(String uuid) {
        final Person person = people.get(uuid);
        if(person==null) { throw new NotFoundException(); }
        return person;
    }
}
