package uk.co.itstherules.model;

import org.junit.Test;

public final class AssetTest {

    @Test(expected = IllegalArgumentException.class)
    public void uuidNullThrows() {
        new Asset(null, "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nameNullThrows() {
        new Asset("", null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialNullThrows() {
        new Asset("", "", null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void notesNullThrows() {
        new Asset("", "", "", null);
    }

}
