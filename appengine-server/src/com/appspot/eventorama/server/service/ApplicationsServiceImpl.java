package com.appspot.eventorama.server.service;

import java.util.List;
import java.util.logging.Logger;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.NotLoggedInException;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ApplicationsServiceImpl implements ApplicationsService {

    private static final Logger log = Logger.getLogger(ApplicationsServiceImpl.class.getName());
    
    
    public List<Application> getList() throws NotLoggedInException {
        checkLoggedIn();
        
        log.info("Logged in. Returning list of apps.");
        
        return null;
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
