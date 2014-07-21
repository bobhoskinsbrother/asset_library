package uk.co.itstherules.server.rest;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import uk.co.itstherules.model.ObjectRepository;
import uk.co.itstherules.storage.DataStore;
import uk.co.itstherules.storage.FileDataStore;

final class AssetServer {

    private final HttpServer server;

    AssetServer() {
        DataStore dataStore = new FileDataStore(System.getProperty("user.home") + "/assets");
        ObjectRepository repository = new ObjectRepository(dataStore);

        server = HttpServer.createSimpleServer("/web/", 9800);
        final ServerConfiguration configuration = server.getServerConfiguration();

        configuration.addHttpHandler(new AssetDispatcher(repository), "/asset");
        configuration.addHttpHandler(new ReturnAssetDispatcher(repository), "/return_asset");
        configuration.addHttpHandler(new ReserveAssetDispatcher(repository), "/reserve_asset");
        configuration.addHttpHandler(new ReserveAssetsDispatcher(repository), "/reserve_assets");
        configuration.addHttpHandler(new IsAvailableDispatcher(repository), "/is_available");
        configuration.addHttpHandler(new AssetsDispatcher(repository), "/assets");
        configuration.addHttpHandler(new PersonDispatcher(repository), "/person");
        configuration.addHttpHandler(new PeopleDispatcher(repository), "/people");
        configuration.addHttpHandler(new CLStaticHttpHandler(AssetServer.class.getClassLoader(), "/web/"), "/");
    }

    private void serve() {
        try {
            System.out.println("Starting the asset server");
            server.start();
            System.out.println("Started the asset server");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            server.shutdown();
        }
    }

    public static void main(String[] args) {
        final AssetServer assetServer = new AssetServer();
        assetServer.serve();
    }

}
