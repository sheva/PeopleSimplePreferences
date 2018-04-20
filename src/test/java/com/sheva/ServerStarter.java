package com.sheva;

import com.sheva.db.DatabaseTestHelper;
import com.sheva.db.PropertiesFileResolver;
import com.sheva.utils.LoggingConfiguration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

/**
 * Entry point to test application.
 *
 * Created by Sheva on 9/28/2016.
 */
public class ServerStarter {

    public static final String applicationURL = PropertiesFileResolver.INSTANCE.getApplicationURL();
    public static final String applicationPath = applicationURL + PropertiesFileResolver.INSTANCE.getContextPath();

    private final HttpServer server;

    private ServerStarter() throws IOException {
        DatabaseTestHelper.prepareDatabase();
        LoggingConfiguration.configure();

        ResourceConfig resConfig = new ResourceConfig().packages("com.sheva.api");
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(applicationPath), resConfig);

        StaticHttpHandler handler = new StaticHttpHandler(Paths.get("src").toAbsolutePath().toString(), "test/web");
        server.getServerConfiguration().addHttpHandler(handler);
    }

    private void launch() throws IOException {
        System.out.println(String.format("Application started at %s\nTest page available at %s\nHit enter to stop it...",
                applicationPath, applicationURL + "/test/web/index.html"));
        System.in.read();
        server.shutdownNow();
    }

    public static void main(String... args) throws Exception {
        new ServerStarter().launch();
    }

    public static HttpServer startHttpServer() throws IOException {
        return new ServerStarter().server;
    }
}
