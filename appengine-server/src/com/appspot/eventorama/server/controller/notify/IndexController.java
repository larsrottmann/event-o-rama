package com.appspot.eventorama.server.controller.notify;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;
import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;

public class IndexController extends Controller {

    private static final Logger log = Logger.getLogger(IndexController.class.getName());
    
    
    @Override
    public Navigation run() throws Exception {
        // TODO: some basic request authentication
        
        Validators v = new Validators(request);
        v.add("id", v.required(), v.longType());
        v.add("url", v.required());
        
        if (!v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters: id=%s (%s), url=%s (%s)", asString("id"), errors.get("id"), asString("url"), errors.get("url")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        
        try {
            Application app = Datastore.get(ApplicationMeta.get(), Datastore.createKey(ApplicationMeta.get(), asLong("id")));
            
            app.setDownloadUrl(asString("url"));
            Datastore.put(app);

            response.setStatus(HttpURLConnection.HTTP_OK);
        }
        catch (EntityNotFoundRuntimeException e)
        {
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        catch (NumberFormatException e)
        {
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        return null;
    }
}
