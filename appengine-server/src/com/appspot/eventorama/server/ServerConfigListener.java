package com.appspot.eventorama.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerConfigListener implements ServletContextListener {

    private static final String CONFIG = "com.appspot.eventorama.server.config";

    public void contextDestroyed(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user
        // request if no warmup request was invoked.
        event.getServletContext().setAttribute(CONFIG, ServerConfig.getInstance());
    }

    public void contextInitialized(ServletContextEvent event) {
        // App Engine does not currently invoke this method.

    }

}
