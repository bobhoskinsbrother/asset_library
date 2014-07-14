package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.model.People;
import uk.co.itstherules.model.Serializer;

public final class PeopleDispatcher extends BaseHttpDispatcher {

    private final ObjectRepository repository;

    public PeopleDispatcher(ObjectRepository repository) {this.repository = repository;}

    public DispatchResponse dispatch(Request request) {
        final People people = repository.allPeople();
        String serializedPeople = Serializer.serialize(people);
        return new DispatchResponse(serializedPeople);
    }
}
