package com.appspot.eventorama.server.controller.download;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class IndexController extends Controller {

    private static final Logger log = Logger.getLogger(IndexController.class.getName());
    
    
    @Override
    public Navigation run() throws Exception {
        Validators v = new Validators(request);
        v.add("id", v.required());

        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid download request: id=%s (%s)", asString("id"), errors.get("id")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        log.fine("Got download request for app id: " + asString("id"));
        
        Application app;
        try {
            Key key = KeyFactory.stringToKey(asString("id"));
            app = Datastore.get(ApplicationMeta.get(), key);
        }
        catch (Exception e)
        {
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return forward("notfound.jsp");
        }
        
        
        if (app.getDownloadUrl() == null)
        {
            response.setStatus(HttpURLConnection.HTTP_ACCEPTED);
            return forward("inprogress.jsp");
        }
        
        return redirect(app.getDownloadUrl());
    }
}
