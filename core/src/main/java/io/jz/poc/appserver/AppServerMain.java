package io.jz.poc.appserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class AppServerMain {

    private final static Logger LOGGER = LoggerFactory.getLogger(AppServer.class);

    private final static String PROP_CONF_PATH = "conf.path";

    public static void main(String[] args) throws Exception{
        AppServerConfig serverConfig;
        if (System.getProperty(PROP_CONF_PATH) != null) {
            LOGGER.info("load config file {}", System.getProperty(PROP_CONF_PATH));
            serverConfig = new ObjectMapper().readValue(new FileInputStream(System.getProperty(PROP_CONF_PATH)), AppServerConfig.class);
        } else {
            LOGGER.info("load default config file");
            serverConfig = new ObjectMapper().readValue(AppServerMain.class.getResourceAsStream("/config.json"), AppServerConfig.class);
        }
        LOGGER.info("Init config of app server: port={} with {} virtual host",serverConfig.getPort(),serverConfig.getVirtualHosts().size());
        AppServer appServer = new AppServer(serverConfig);
        appServer.start();
    }

}
