package com.sheva.jbehave.stories;

import com.sheva.ServerStarter;
import com.sheva.api.providers.FoodCollectionMessageBodyWriter;
import com.sheva.api.providers.PersonCollectionMessageBodyWriter;
import com.sheva.api.providers.PersonMessageBodyHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Arrays;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;
import static org.jbehave.core.reporters.Format.*;

/**
 * Container runner class for JBehave stories.
 *
 * Created by Sheva on 9/27/2016.
 */
public class AppStories extends JUnitStories {

    private Configuration configuration;
    private static Client client;
    private static HttpServer server;

    public AppStories() {
        configuration = new MostUsefulConfiguration();
        configuration.useStoryReporterBuilder(new StoryReporterBuilder().withFormats(STATS, CONSOLE, HTML));
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = ServerStarter.startServer();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(PersonMessageBodyHandler.class);
        clientConfig.register(FoodCollectionMessageBodyWriter.class);
        clientConfig.register(PersonCollectionMessageBodyWriter.class);
        client = ClientBuilder.newBuilder().withConfig(clientConfig).build();
    }

    @AfterClass
    public static void tearDownClass() {
        client.close();
        server.shutdownNow();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    static WebTarget getTarget() {
        return client.target(ServerStarter.BASE_URI);
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(),
                Arrays.asList(
                        new FoodReadScenarios(),
                        new FoodUpdateScenarios(),
                        new FoodCreateScenarios(),
                        new FoodDeleteScenarios(),
                        new PersonReadScenarios(),
                        new PersonUpdateScenarios(),
                        new PersonCreateScenarios(),
                        new PersonDeleteScenarios()
                ));
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromPath("src/test/resources"), "**/*.story", "");
    }
}
