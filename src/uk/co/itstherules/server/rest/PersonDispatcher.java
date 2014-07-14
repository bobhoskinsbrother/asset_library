package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.Request;
import uk.co.itstherules.model.*;

public final class PersonDispatcher extends SingleObjectDispatcher {

    private final ObjectRepository repository;

    public PersonDispatcher(ObjectRepository repository) {
        this.repository = repository;
    }

    protected DispatchResponse storeAndRespond(Request request) {
        final String personString = request.getParameter("person");
        final Person person = Deserializer.deserializePerson(personString);
        repository.add(person);
        return respondWithSuccess();
    }


    protected DispatchResponse retrieveAndRespond(String uuid) {
        try {
            final Person person = repository.getPerson(uuid);
            String serializedPerson = Serializer.serialize(person);
            return new DispatchResponse(serializedPerson);
        } catch (NotFoundException e) {
            return respondWithNotFound();
        }
    }
}
