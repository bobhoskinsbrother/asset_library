package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.storage.DataStore;
import uk.co.itstherules.storage.FileDataStore;

public final class AssetServer {

    private HttpServer server;

    public AssetServer() {
        DataStore dataStore = new FileDataStore(System.getProperty("user.home") + "/assets");
        ObjectRepository repository = new ObjectRepository(dataStore);

        server = HttpServer.createSimpleServer("/web/", 9800);
        final ServerConfiguration configuration = server.getServerConfiguration();

        configuration.addHttpHandler(new AssetDispatcher(repository), "/asset");
        configuration.addHttpHandler(new ReserveAssetDispatcher(repository), "/reserve_asset");
        configuration.addHttpHandler(new IsAvailableDispatcher(repository), "/is_available");
        configuration.addHttpHandler(new AssetsDispatcher(repository), "/assets");
        configuration.addHttpHandler(new PersonDispatcher(repository), "/person");
        configuration.addHttpHandler(new PeopleDispatcher(repository), "/people");
        configuration.addHttpHandler(new CLStaticHttpHandler(AssetServer.class.getClassLoader(), "/web/"), "/");
    }

    private void serve() {
        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        final AssetServer assetServer = new AssetServer();
        assetServer.serve();
    }

}
