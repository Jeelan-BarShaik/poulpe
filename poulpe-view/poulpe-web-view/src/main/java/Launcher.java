
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dionis
 *         7/6/12 2:49 AM
 */
public class Launcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);

        server.addConnector(connector);
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/");
//        webAppContext.setWar("src/main/webapp");
        webAppContext.setWar("poulpe-view/poulpe-web-view/src/main/webapp");
        final HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[]{webAppContext});
        server.setHandler(handlerList);

        server.start();
        long end = System.currentTimeMillis();
        LOGGER.info("Initialization took {} ms", end-start);
        server.join();
    }
}