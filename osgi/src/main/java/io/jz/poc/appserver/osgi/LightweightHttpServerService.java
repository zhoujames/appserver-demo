package io.jz.poc.appserver.osgi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jz.poc.appserver.AppServer;
import io.jz.poc.appserver.AppServerConfig;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Map;

@Service(LightweightHttpServerService.class)
@Component(immediate = true, metatype = true, enabled = true)
public class LightweightHttpServerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightweightHttpServerService.class);

    @Property(value = "/apps/appserver/config.json")
    static final String CONF_PATH = "conf.path";

    @Reference
    SlingRepository repository;

    AppServer appServer;

    @Activate
    public final void activate(final Map<String, Object> props) throws Exception {
        final Session session = repository.loginAdministrative(null);
        LOGGER.info("activate Http Server Service");
        String confPath = (String) props.get(CONF_PATH);
        if (confPath.startsWith("/")){
            confPath = confPath.substring(1);
        }
        AppServerConfig serverConfig;
        LOGGER.info("load config file {}",confPath);
        final Node rootNode = session.getRootNode();
        if (!rootNode.hasNode(confPath)) {
            LOGGER.error("conf path is not valid");
            return;
        }
        final Node node = rootNode.getNode(confPath);
        serverConfig = new ObjectMapper().readValue(node.getNode("jcr:content").getProperty("jcr:data").getStream(), AppServerConfig.class);
        session.logout();
        LOGGER.info("Init config of app server: port={} with {} virtual host", serverConfig.getPort(), serverConfig.getVirtualHosts().size());
        appServer = new AppServer(serverConfig);

        new Thread(new Runnable() {
            public void run() {
                try {
                    appServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Deactivate
    public void destroy() throws Exception {
        LOGGER.info("destroy OnlineService Wrapper Client");
        if (appServer != null) {
            appServer.stop();
        }
        appServer = null;
    }
}
