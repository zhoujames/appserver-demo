package io.jz.poc.appserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jz.poc.appserver.plugin.FileContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppServerConfigTest {

    @Test
    public void testLoadAppConfig() throws Exception{
        AppServerConfig serverConfig = new ObjectMapper().readValue(
                getClass().getResourceAsStream("/config-test.json"), AppServerConfig.class);
        assertEquals("port mismatch", serverConfig.getPort(), 8080);
        assertEquals("virtual host error", serverConfig.getVirtualHosts().size(), 3);
        assertTrue("context expected", serverConfig.getVirtualHosts().get(0).getContexts().size() > 0);
        assertTrue("file context expected",serverConfig.getVirtualHosts().get(0).getContexts().get(0) instanceof FileContext);
    }

}