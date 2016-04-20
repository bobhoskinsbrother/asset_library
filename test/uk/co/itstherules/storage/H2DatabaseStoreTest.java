package uk.co.itstherules.storage;

import org.junit.Before;
import org.junit.Test;
import uk.co.itstherules.model.Asset;
import uk.co.itstherules.model.Person;
import uk.co.itstherules.model.ReserveAsset;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class H2DatabaseStoreTest {

    private String connectionString;
    private String dbPath;

    @Before
    public void clean() throws Exception {
        File tmpDirForDb = new File(System.getProperty("java.io.tmpdir") + "/h2test/");
        dbPath = tmpDirForDb.getAbsolutePath() + "/asset_library";
        connectionString = "jdbc:h2:file:" + tmpDirForDb.getAbsolutePath() + "/asset_library";
        zapTheData();
    }

    private void zapTheData() throws Exception {
        Class.forName("org.h2.Driver");
        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'", connection);

        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            String truncate = "TRUNCATE TABLE " + tableName;
            PreparedStatement statement = connection.prepareStatement(truncate);
            statement.execute();
            statement.close();

        }
        connection.close();
    }

    @Test
    public void willInitiateTablesOnConstruction() throws Exception {

        new H2DatabaseStore(dbPath);

        Class.forName("org.h2.Driver");
        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC'", connection);

        List<String> result = new ArrayList<>();

        while (rs.next()) {
            result.add(rs.getString("TABLE_NAME"));
        }
        assertThat(result, hasItems("RESERVEASSET", "ASSET", "PERSON"));
        connection.close();
    }

    @Test
    public void canStoreAsset() throws Exception {
        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        Asset asset = new Asset("qwertyuiopasdfghjklzxcvbnm", "iPhone 10", "12345678901234567890", "some notes");

        unit.store(asset);

        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT uuid, title, serialnumber, notes FROM asset where uuid='qwertyuiopasdfghjklzxcvbnm'", connection);

        rs.next();

        assertThat(rs.getString("uuid"), is("qwertyuiopasdfghjklzxcvbnm"));
        assertThat(rs.getString("title"), is("iPhone 10"));
        assertThat(rs.getString("serialnumber"), is("12345678901234567890"));
        assertThat(rs.getString("notes"), is("some notes"));
        connection.close();
    }


    @Test
    public void canStoreAssetThenUpdateIt() throws Exception {

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        Asset asset = new Asset("qwertyuiopasdfghjklzxcvb", "iPhone 10", "12345678901234567890", "some notes");

        unit.store(asset);

        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT uuid, title, serialnumber, notes FROM asset", connection);

        rs.next();

        assertThat(rs.getString("uuid"), is("qwertyuiopasdfghjklzxcvb"));
        assertThat(rs.getString("title"), is("iPhone 10"));
        assertThat(rs.getString("serialnumber"), is("12345678901234567890"));
        assertThat(rs.getString("notes"), is("some notes"));

        asset = new Asset("qwertyuiopasdfghjklzxcvb", "iPhone 12s", "09876543210987654321", "some more notes");

        unit.store(asset);

        connection = getJdbcConnection();
        rs = executeQuesry("SELECT uuid, title, serialnumber, notes FROM asset", connection);

        rs.next();

        assertThat(rs.getString("uuid"), is("qwertyuiopasdfghjklzxcvb"));
        assertThat(rs.getString("title"), is("iPhone 12s"));
        assertThat(rs.getString("serialnumber"), is("09876543210987654321"));
        assertThat(rs.getString("notes"), is("some more notes"));

        connection.close();
    }

    @Test
    public void canStoreReserveAsset() throws Exception {

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);

        ReserveAsset reserveAsset = new ReserveAsset("qwertyuioplkjhgfdsazxcvbnm", "poiuytrewqlkjhgfdsamnbvcx", "qwertyuiopasdfghjklzxcvbnq");

        unit.store(reserveAsset);

        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT uuid, assetuuid, personuuid FROM reserveasset", connection);

        rs.next();

        assertThat(rs.getString("uuid"), is("qwertyuioplkjhgfdsazxcvbnm"));
        assertThat(rs.getString("assetuuid"), is("qwertyuiopasdfghjklzxcvbnq"));
        assertThat(rs.getString("personuuid"), is("poiuytrewqlkjhgfdsamnbvcx"));
        connection.close();
    }

    @Test
    public void canStorePerson() throws Exception {

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        Person person = new Person("qwertyuiopgfdsahjklmnbvcxz", "Bob", "the Builder");

        unit.store(person);

        Connection connection = getJdbcConnection();
        ResultSet rs = executeQuesry("SELECT uuid, firstname, lastname FROM person", connection);

        rs.next();

        assertThat(rs.getString("uuid"), is("qwertyuiopgfdsahjklmnbvcxz"));
        assertThat(rs.getString("firstname"), is("Bob"));
        assertThat(rs.getString("lastname"), is("the Builder"));
        connection.close();
    }

    @Test
    public void canRetrieveAsset() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO ASSET (uuid, title, serialnumber, notes) VALUES ('qwertyuiop','the_title','the_serial','the_notes')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        Asset asset = unit.retrieveAsset("qwertyuiop");

        assertThat(asset.getUuid(), is("qwertyuiop"));
        assertThat(asset.getName(), is("the_title"));
        assertThat(asset.getSerialNumber(), is("the_serial"));
        assertThat(asset.getNotes(), is("the_notes"));

    }

    @Test
    public void canRetrieveReserveAsset() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO RESERVEASSET (uuid, assetuuid, personuuid) VALUES ('qwertyuiop','assetid','personid')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        ReserveAsset asset = unit.retrieveReserveAsset("qwertyuiop");

        assertThat(asset.getUuid(), is("qwertyuiop"));
        assertThat(asset.getAssetUuid(), is("assetid"));
        assertThat(asset.getPersonUuid(), is("personid"));

    }

    @Test
    public void canRetrievePerson() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO PERSON (uuid, firstname, lastname) VALUES ('zxcvbnm','more','names')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        Person person = unit.retrievePerson("zxcvbnm");

        assertThat(person.getUuid(), is("zxcvbnm"));
        assertThat(person.getFirstName(), is("more"));
        assertThat(person.getLastName(), is("names"));

    }

    @Test
    public void canRemoveAsset() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO ASSET (uuid, title, serialnumber, notes) VALUES ('qwertyuiop','the_title','the_serial','the_notes')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        unit.removeAsset("qwertyuiop");

        ResultSet resultSet = executeQuesry("SELECT uuid from ASSET WHERE uuid='qwertyuiop'", connection);

        assertThat(resultSet.next(), is(false));
    }

    @Test
    public void canRemoveReserveAsset() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO RESERVEASSET (uuid, assetuuid, personuuid) VALUES ('qwertyuiop','assetid','personid')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        unit.removeReserveAsset("qwertyuiop");

        ResultSet resultSet = executeQuesry("SELECT uuid from RESERVEASSET WHERE uuid='qwertyuiop'", connection);

        assertThat(resultSet.next(), is(false));
    }

    @Test
    public void canRemovePerson() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO PERSON (uuid, firstname, lastname) VALUES ('zxcvbnm','more','names')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        unit.removePerson("zxcvbnm");

        ResultSet resultSet = executeQuesry("SELECT uuid from PERSON WHERE uuid='zxcvbnm'", connection);

        assertThat(resultSet.next(), is(false));
    }

    @Test
    public void canRetrieveAssets() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO ASSET (uuid, title, serialnumber, notes) VALUES ('one','the_first_title','the_first_serial','the_first_notes')", connection);
        execute("INSERT INTO ASSET (uuid, title, serialnumber, notes) VALUES ('two','the_second_title','the_second_serial','the_second_notes')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        List<Asset> assets = unit.retrieveAllAssets();

        assertThat(assets.size(), is(2));
        assertThat(assets.get(0).getUuid(), is("one"));
        assertThat(assets.get(1).getUuid(), is("two"));
    }

    @Test
    public void canRetrieveReserveAssets() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO RESERVEASSET (uuid, assetuuid, personuuid) VALUES ('one','assetid1','personid1')", connection);
        execute("INSERT INTO RESERVEASSET (uuid, assetuuid, personuuid) VALUES ('two','assetid2','personid2')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        List<ReserveAsset> assets = unit.retrieveAllReservedAssets();

        assertThat(assets.size(), is(2));
        assertThat(assets.get(0).getUuid(), is("one"));
        assertThat(assets.get(1).getUuid(), is("two"));
    }

    @Test
    public void canRetrievePeople() throws Exception {
        Connection connection = getJdbcConnection();

        execute("INSERT INTO PERSON (uuid, firstname, lastname) VALUES ('one','more1','names1')", connection);
        execute("INSERT INTO PERSON (uuid, firstname, lastname) VALUES ('two','more2','names2')", connection);

        H2DatabaseStore unit = new H2DatabaseStore(dbPath);
        List<Person> people = unit.retrieveAllPeople();

        assertThat(people.size(), is(2));
        assertThat(people.get(0).getUuid(), is("one"));
        assertThat(people.get(1).getUuid(), is("two"));

    }


    private ResultSet executeQuesry(String sql, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement.executeQuery();
    }

    private void execute(String sql, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
    }

    private Connection getJdbcConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String user = "sa";
        return DriverManager.getConnection(connectionString, user, null);
    }


}
