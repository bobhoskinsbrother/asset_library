package uk.co.itstherules.server.rest;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public final class UriParserTest {

    @Test(expected = IllegalArgumentException.class) public void noNullsAllowed() {
        new UriParser(null);
    }

    @Test public void emptyStringsAllowed() {
        final UriParser unit = new UriParser("");
        assertThat(unit.getDispatcher(), is(""));
        assertThat(unit.getUuid(), is(""));
    }

    @Test public void oneSlashAllowed() {
        final UriParser unit = new UriParser("/");
        assertThat(unit.getDispatcher(), is(""));
        assertThat(unit.getUuid(), is(""));
    }

    @Test public void dispatcherOnlyNoSlashAllowed() {
        final UriParser unit = new UriParser("fred");
        assertThat(unit.getDispatcher(), is("fred"));
        assertThat(unit.getUuid(), is(""));
    }

    @Test public void dispatcherOnlyWithSlashAllowed() {
        final UriParser unit = new UriParser("/fred");
        assertThat(unit.getDispatcher(), is("fred"));
        assertThat(unit.getUuid(), is(""));
    }

    @Test public void dispatcherOnlyWithASlashAllowed() {
        final UriParser unit = new UriParser("/fred/");
        assertThat(unit.getDispatcher(), is("fred"));
        assertThat(unit.getUuid(), is(""));
    }

    @Test public void dispatcherAndUuid() {
        final UriParser unit = new UriParser("/fred/wilma");
        assertThat(unit.getDispatcher(), is("fred"));
        assertThat(unit.getUuid(), is("wilma"));
    }

    @Test public void dispatcherAndUuidWithSomeMore() {
        final UriParser unit = new UriParser("/fred/wilma/barney/whatever");
        assertThat(unit.getDispatcher(), is("fred"));
        assertThat(unit.getUuid(), is("wilma"));
    }


}
