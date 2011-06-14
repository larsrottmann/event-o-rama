package com.appspot.eventorama.server.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.slim3.datastore.Datastore;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.NotLoggedInException;
import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ApplicationsServiceImpl implements ApplicationsService {

    private static final Logger log = Logger.getLogger(ApplicationsServiceImpl.class.getName());
    
    
    public List<Application> getList() throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Logged in. Querying list of apps.");
        
        ApplicationMeta meta = ApplicationMeta.get();
        List<Application> apps = new ArrayList<Application>(); 
//        apps = Datastore.query(meta).filter(meta.user.equal(getUser())).asList();
        
        Application app1 = new Application();
        app1.setTitle("app1");
        app1.setStartDate(new Date(System.currentTimeMillis()));
        app1.setExpirationDate(new Date(System.currentTimeMillis()));
        Application app2 = new Application();
        app2.setTitle("app2");
        app2.setStartDate(new Date(System.currentTimeMillis()));
        app2.setExpirationDate(new Date(System.currentTimeMillis()));
        apps.addAll(Arrays.asList(app1, app2));
        log.info("" + apps);

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
