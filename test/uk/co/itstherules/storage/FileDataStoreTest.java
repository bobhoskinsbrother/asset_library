package uk.co.itstherules.storage;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.itstherules.FileTestHelper.*;
import static uk.co.itstherules.storage.DataStore.Section.Asset;

public final class FileDataStoreTest {

    private String path;

    @Before public void setup() {
        path = System.getProperty("java.io.tmpdir") + "/" + "test_dir";
        final File rootDir = new File(path);
        deleteContentsOf(rootDir);
        final File assetDir = new File(rootDir, "asset");
        assetDir.mkdirs();
    }

    @Test public void canWriteFileThatDoesNotAlreadyExist() throws Exception {
        final DataStore dataStore = new FileDataStore(path);
        dataStore.store(Asset, "one", "This is a bit of text");
        String readFile = readFileToString(new File(path, "asset/one"));
        assertThat(readFile, is("This is a bit of text"));
    }

    @Test public void canOverwriteFileThatAlreadyExist() throws Exception {
        final DataStore dataStore = new FileDataStore(path);
        dataStore.store(Asset, "one", "This is a bit of text");
        String readFile = readFileToString(new File(path, "asset/one"));
        assertThat(readFile, is("This is a bit of text"));
        dataStore.store(Asset, "one", "This is a different bit of text");
        readFile = readFileToString(new File(path, "asset/one"));
        assertThat(readFile, is("This is a different bit of text"));
    }

    @Test public void cannotReadFileThatDoesNotExist() throws Exception {
        final DataStore dataStore = new FileDataStore(path);
        final String reply = dataStore.retrieve(Asset, "fred");
        assertThat(reply, is(""));
    }

    @Test public void canReadFileThatExists() throws Exception {
        writeFileFromString(new File(path, "asset/quidgibo"), "this is an example piece of text to read");
        final DataStore dataStore = new FileDataStore(path);
        final String reply = dataStore.retrieve(Asset, "quidgibo");
        assertThat(reply, is("this is an example piece of text to read"));
    }

    @Test public void canReadAllFilesThatExists() throws Exception {
        writeFileFromString(new File(path, "asset/quidgibo"), "this is an example piece of text to read");
        writeFileFromString(new File(path, "asset/freddy"), "this is blah blah blah");
        final DataStore dataStore = new FileDataStore(path);
        final Map<String, String> reply = dataStore.retrieveAll(Asset);
        assertThat(reply.size(), is(2));
        assertThat(reply.get("quidgibo"), is("this is an example piece of text to read"));
        assertThat(reply.get("freddy"), is("this is blah blah blah"));
    }

}
