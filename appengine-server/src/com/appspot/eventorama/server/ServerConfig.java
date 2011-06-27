package com.appspot.eventorama.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Properties;

public class ServerConfig extends java.util.Properties {

    private static final long serialVersionUID = -331186015444245483L;

    private static final Logger log = Logger.getLogger(ServerConfig.class.getName());
    
    private ServerConfig() {
        super();

        loadConfig();
    }

    private ServerConfig(Properties defaults) {
        super(defaults);
        
        loadConfig();
    }

    private void loadConfig() {
        try {
            FileInputStream in = new FileInputStream(System.getProperty("com.appspot.eventorama.server.config"));
            load(in);
            in.close();
        } catch (IOException e) {
            log.severe("Could not load service.properties config file.");
        }
    }

    private static class SingletonHolder { 
        public static final ServerConfig INSTANCE = new ServerConfig();
    }
    
    
    synchronized public static ServerConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
