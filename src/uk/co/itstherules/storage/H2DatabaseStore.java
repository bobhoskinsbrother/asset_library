package uk.co.itstherules.storage;

import uk.co.itstherules.model.Asset;
import uk.co.itstherules.model.Person;
import uk.co.itstherules.model.ReserveAsset;

import java.sql.*;
import java.util.*;

import static uk.co.itstherules.storage.CollectionsConverter.string;
import static uk.co.itstherules.storage.CollectionsConverter.map;

public class H2DatabaseStore implements DataStore {

    private static final String ASSET = "asset";
    private static final String PERSON = "person";
    private static final String RESERVEASSET = "reserveasset";

    private final Connection connection;

    public H2DatabaseStore(String path) {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:file:" + path, "sa", "");
            connection.setAutoCommit(true);
            setupTablesIfNotExists(connection);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }

    @Override
    public void store(Asset asset) {
        store(ASSET, asset.getUuid(), map("title", asset.getName(), "serialnumber", asset.getSerialNumber(), "notes", asset.getNotes()));
    }

    @Override
    public void store(Person person) {
        store(PERSON, person.getUuid(), map("firstname", person.getFirstName(), "lastname", person.getLastName()));
    }

    @Override
    public void store(ReserveAsset reserveAsset) {
        store(RESERVEASSET, reserveAsset.getUuid(), map("assetuuid", reserveAsset.getAssetUuid(), "personuuid", reserveAsset.getPersonUuid()));

    }

    @Override
    public void removeAsset(String uuid) {
        removeAsset(ASSET, uuid);
    }

    @Override
    public void removePerson(String uuid) {
        removeAsset(PERSON, uuid);
    }

    @Override
    public void removeReserveAsset(String uuid) {
        removeAsset(RESERVEASSET, uuid);
    }

    @Override
    public Asset retrieveAsset(String uuid) {
        ResultSet resultSet = selectSingle(ASSET, uuid, "uuid", "title", "serialNumber", "notes");

        Asset asset;
        try {
            resultSet.next();
            asset = new Asset(resultSet.getString("uuid"), resultSet.getString("title"), resultSet.getString("serialNumber"), resultSet.getString("notes"));
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return asset;
    }

    @Override
    public Person retrievePerson(String uuid) {
        ResultSet resultSet = selectSingle(PERSON, uuid, "uuid", "firstname", "lastname");

        Person person;
        try {
            resultSet.next();
            person = new Person(resultSet.getString("uuid"), resultSet.getString("firstname"), resultSet.getString("lastname"));
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return person;
    }

    @Override
    public ReserveAsset retrieveReserveAsset(String uuid) {
        ResultSet resultSet = selectSingle(RESERVEASSET, uuid, "uuid", "personuuid", "assetuuid");

        ReserveAsset reserveAsset;
        try {
            resultSet.next();
            reserveAsset = new ReserveAsset(resultSet.getString("uuid"), resultSet.getString("personuuid"), resultSet.getString("assetuuid"));
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reserveAsset;
    }

    @Override
    public List<Person> retrieveAllPeople() {
        ResultSet resultSet = select(PERSON, "uuid", "firstname", "lastname");
        List<Person> people = new ArrayList<>();
        try {
            while (resultSet.next()) {
                people.add(new Person(resultSet.getString("uuid"), resultSet.getString("firstname"), resultSet.getString("lastname")));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return people;
    }

    @Override
    public List<Asset> retrieveAllAssets() {
        ResultSet resultSet = select(ASSET, "uuid", "title", "serialNumber", "notes");
        List<Asset> assets = new ArrayList<>();
        try {
            while(resultSet.next()) {
                assets.add(new Asset(resultSet.getString("uuid"), resultSet.getString("title"), resultSet.getString("serialNumber"), resultSet.getString("notes")));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return assets;
    }

    @Override
    public List<ReserveAsset> retrieveAllReservedAssets() {
        ResultSet resultSet = select(RESERVEASSET, "uuid", "personuuid", "assetuuid");
        List<ReserveAsset> reserveAssets = new ArrayList<>();
        try {
            while(resultSet.next()) {
                reserveAssets.add(new ReserveAsset(resultSet.getString("uuid"), resultSet.getString("personuuid"), resultSet.getString("assetuuid")));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reserveAssets;
    }

    /* private */

    private void update(String section, String uuid, Map<String, String> nameValues) {
        nameValues = new LinkedHashMap<>(nameValues);
        String updateStatement = buildUpdateStatement(section, nameValues);
        Iterator<String> keys;

        try {
            PreparedStatement statement = connection.prepareStatement(updateStatement);
            keys = nameValues.keySet().iterator();
            int i = 1;
            while (keys.hasNext()) {
                String value = nameValues.get(keys.next());
                statement.setString(i, value);
                i++;
            }
            statement.setString(i, uuid);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void store(String section, String uuid, Map<String, String> nameValues) {
        if (!exists(section, uuid)) {
            insert(section, uuid, nameValues);
        } else {
            update(section, uuid, nameValues);
        }
    }

    private ResultSet select(String section, String... columnNames) {
        String insertStatement = buildSelectStatement(section, columnNames);
        try {
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSet selectSingle(String section, String uuid, String... columnNames) {
        String insertStatement = buildSelectSingleStatement(section, columnNames);
        try {
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            statement.setString(1, uuid);
            return statement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exists(String section, String uuid) {
        boolean reply = false;
        try {
            PreparedStatement statement = connection.prepareStatement("select uuid from " + section.toString() + " where uuid = ?");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            reply = resultSet.next();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return reply;
    }

    private void insert(String section, String uuid, Map<String, String> nameValues) {
        nameValues = new LinkedHashMap<>(nameValues);
        String insertStatement = buildInsertStatement(section, nameValues);
        Iterator<String> keys;

        try {
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            keys = nameValues.keySet().iterator();
            statement.setString(1, uuid);
            int i = 2;
            while (keys.hasNext()) {
                String value = nameValues.get(keys.next());
                statement.setString(i, value);
                i++;
            }
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeAsset(String section, String uuid) {
        String deleteStatement = buildDeleteStatement(section);

        try {
            PreparedStatement statement = connection.prepareStatement(deleteStatement);
            statement.setString(1, uuid);
            statement.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildSelectStatement(String section, String[] columnNames) {
        return "SELECT " + string(columnNames, ",") + " FROM " + section;
    }

    private String buildSelectSingleStatement(String section, String[] columnNames) {
        return buildSelectStatement(section, columnNames) + " where uuid = ? ";
    }

    private String buildDeleteStatement(String section) {
        return "DELETE FROM " + section + " where uuid = ? ";
    }

    private String buildInsertStatement(String section, Map<String, String> nameValues) {
        String columns = "";
        String questionMarks = "";
        Iterator<String> keys = nameValues.keySet().iterator();
        while (keys.hasNext()) {
            columns += keys.next();
            questionMarks += "?";
            if (keys.hasNext()) {
                columns += ",";
                questionMarks += ",";
            }
        }
        return "INSERT INTO " + section + " (uuid, " + columns + ") VALUES (?," + questionMarks + ")";
    }

    private String buildUpdateStatement(String section, Map<String, String> nameValues) {
        String columns = "";
        Iterator<String> keys = nameValues.keySet().iterator();
        while (keys.hasNext()) {
            columns += keys.next();
            columns += " = ?";
            if (keys.hasNext()) {
                columns += ",";
            }
        }
        return "UPDATE " + section + " SET " + columns + " WHERE uuid = ?";
    }

    private void setupTablesIfNotExists(Connection connection) throws SQLException {
        String setupTableTemplate = "CREATE TABLE IF NOT EXISTS ";
        setupAssetTableIfNotExists(connection, setupTableTemplate);
        setupPersonTableIfNotExists(connection, setupTableTemplate);
        setupReserveAssetTableIfNotExists(connection, setupTableTemplate);
    }

    private void setupReserveAssetTableIfNotExists(Connection connection, String setupTableTemplate) throws SQLException {
        String create = setupTableTemplate + " " + RESERVEASSET + " (" +
                " uuid varchar(36) NOT NULL, " +
                " personuuid varchar(36) NOT NULL, " +
                " assetuuid varchar(36) NOT NULL," +
                " PRIMARY KEY (uuid)" +
                ")";
        PreparedStatement statement = connection.prepareStatement(create);
        statement.executeUpdate();
        statement.close();
    }

    private void setupPersonTableIfNotExists(Connection connection, String setupTableTemplate) throws SQLException {
        String create = setupTableTemplate + " " + PERSON + " (" +
                " uuid varchar(36) NOT NULL, " +
                " firstname varchar(500) NOT NULL, " +
                " lastname varchar(500)," +
                " PRIMARY KEY (uuid)" +
                ")";
        PreparedStatement statement = connection.prepareStatement(create);
        statement.executeUpdate();
        statement.close();
    }

    private void setupAssetTableIfNotExists(Connection connection, String setupTableTemplate) throws SQLException {
        String create = setupTableTemplate + " " + ASSET + " (" +
                " uuid varchar(36) NOT NULL, " +
                " title varchar(500) NOT NULL, " +
                " serialnumber varchar(500)," +
                " notes varchar(5000)," +
                " PRIMARY KEY (uuid)" +
                ")";
        PreparedStatement statement = connection.prepareStatement(create);
        statement.executeUpdate();
        statement.close();
    }

}
