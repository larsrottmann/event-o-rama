package com.appspot.eventorama.server.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.slim3.datastore.Datastore;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.NotLoggedInException;
import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ApplicationsServiceImpl implements ApplicationsService {

    private static final Logger log = Logger.getLogger(ApplicationsServiceImpl.class.getName());
    
    
    public List<Application> getList() throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Logged in. Querying list of apps.");
        
        ApplicationMeta meta = ApplicationMeta.get();
        List<Application> apps = Datastore.query(meta).filter(meta.user.equal(getUser())).asList();

        // XXX workaround because serializing GAE user object throws an exception
        for (Application app : apps) {
            app.setUser(null);
        }

        return apps;
    }

    public void create(String title, Date startDate, Date expirationDate) throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Creating new application.");
        
        Application app = new Application();
        app.setTitle(title);
        app.setStartDate(startDate);
        app.setExpirationDate(expirationDate);
        app.setUser(getUser());
        
        Datastore.put(app);
    }

    public void delete(Key appKey) throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Deleting application with key=" + appKey);
        
        Datastore.delete(appKey);
    }

    public Application show(Key appKey) throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Logged in. Querying app.");
        
        Application app = Datastore.get(ApplicationMeta.get(), appKey);

        // XXX workaround because serializing GAE user object throws an exception
        app.setUser(null);
        
        return app;
    }


    
    private void checkLoggedIn() throws NotLoggedInException {
        if (getUser() == null) {
            log.warning("Not logged in.");
            throw new NotLoggedInException("Not logged in.");
          }
    }

    private User getUser() {
        UserService userService = UserServiceFactory.getUserService();
        return userService.getCurrentUser();
    }


}
