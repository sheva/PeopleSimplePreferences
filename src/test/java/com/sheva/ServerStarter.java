package com.sheva;

import com.sheva.db.DatabaseTestHelper;
import com.sheva.utils.LoggingConfiguration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.nio.file.Paths;

/**
 * Entry point to test application.
 *
 * Created by Sheva on 9/28/2016.
 */
public class ServerStarter {

    public static final String HOST_PORT = "http://0.0.0.0:8080/";
    public static final String BASE_URI = HOST_PORT + "preferences/";

    public static HttpServer startServer() throws Exception {
        prepareDb();

        final ResourceConfig resourceConfig = new ResourceConfig().packages("com.sheva.api");
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), resourceConfig);
        StaticHttpHandler staticHttpHandler = new StaticHttpHandler(Paths.get("src").toAbsolutePath().toString(), "test/web");
        httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler);

        return httpServer;
    }

    private static void prepareDb() throws Exception {
        DatabaseTestHelper.deleteAllData();
        DatabaseTestHelper.loadTestData();
    }

    public static void main(String... args) throws Exception {
        LoggingConfiguration.configure();
        HttpServer server = startServer();
        System.out.println(String.format("Application started at %s\nTest page available at %s\nHit enter to stop it...",
                BASE_URI, HOST_PORT + "test/web/"));
        System.in.read();
        server.shutdownNow();
    }
}
