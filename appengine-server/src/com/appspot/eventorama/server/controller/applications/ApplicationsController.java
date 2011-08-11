package com.appspot.eventorama.server.controller.applications;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Errors;
import org.slim3.controller.validator.Validators;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.server.util.ApplicationHelper;
import com.appspot.eventorama.shared.model.Application;

public class ApplicationsController extends Controller {

    private static final Logger log = Logger.getLogger(ApplicationsController.class.getName());
    
    
    @Override
    protected Navigation run() throws Exception {
        Validators v = new Validators(request);
        v.add("id", v.required());

        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters: id=%s (%s)", asString("id"), errors.get("id")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }

        Application app = null;
        try
        {
            app = Datastore.get(ApplicationMeta.get(), asKey("id"));
        }
        catch (EntityNotFoundRuntimeException e)
        {
            log.warning("App not found: " + asString("id"));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            return null;
        }
        catch (Exception e)
        {
            log.warning(String.format("Not a valid app id: id=%s", asString("id")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }

        log.info("Sending application JSON payload: " + ApplicationMeta.get().modelToJson(app));

        response.setHeader("content-type", "application/json; charset=utf-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
        writer.write(ApplicationHelper.applicationToJsonObject(app).toString());
        writer.flush();

        return null;
    }

}
