package uk.co.itstherules.model;

public final class Check {

    public static Check that() {
        return new Check();
    }

    public Check isNotNull(Object object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        return this;
    }

}
