import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {


    public static void main(String[] args) throws Exception {
        Properties properties = loadConfiguration();
        initServer(properties);
    }

    private static void initServer(Properties properties) throws Exception {
        Integer port = Integer.valueOf(properties.getProperty("server.port"));
        String host = properties.getProperty("server.host");

        Integer clientId = Integer.valueOf(properties.getProperty("client.id"));
        String clientSecret = properties.getProperty("client.secret");

        HandlerCollection handlers = new HandlerCollection();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase(Main.class.getResource("/static").getPath());

        VkApiClient vk = new VkApiClient(new HttpTransportClient());
        handlers.setHandlers(new Handler[]{resourceHandler, new RequestHandler(vk, clientId, clientSecret, host)});

        Server server = new Server(port);
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream inputStream = Main.class.getResourceAsStream("/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
