package com.appspot.eventorama.server.controller.notify;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.json.JSONTokener;
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
    
    private static final String URL_PATTERN = new StringBuilder()
            .append("((?:(http|https|Http|Https|rtsp|Rtsp):")
            .append("\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)")
            .append("\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_")
            .append("\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?")
            .append("((?:(?:[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}\\.)+")   // named host
            .append("(?:")   // plus top level domain
            .append("(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])")
            .append("|(?:biz|b[abdefghijmnorstvwyz])")
            .append("|(?:cat|com|coop|c[acdfghiklmnoruvxyz])")
            .append("|d[ejkmoz]")
            .append("|(?:edu|e[cegrstu])")
            .append("|f[ijkmor]")
            .append("|(?:gov|g[abdefghilmnpqrstuwy])")
            .append("|h[kmnrtu]")
            .append("|(?:info|int|i[delmnoqrst])")
            .append("|(?:jobs|j[emop])")
            .append("|k[eghimnrwyz]")
            .append("|l[abcikrstuvy]")
            .append("|(?:mil|mobi|museum|m[acdghklmnopqrstuvwxyz])")
            .append("|(?:name|net|n[acefgilopruz])")
            .append("|(?:org|om)")
            .append("|(?:pro|p[aefghklmnrstwy])")
            .append("|qa")
            .append("|r[eouw]")
            .append("|s[abcdeghijklmnortuvyz]")
            .append("|(?:tel|travel|t[cdfghjklmnoprtvwz])")
            .append("|u[agkmsyz]")
            .append("|v[aceginu]")
            .append("|w[fs]")
            .append("|y[etu]")
            .append("|z[amw]))")
            .append("|(?:(?:25[0-5]|2[0-4]") // or ip address
            .append("[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]")
            .append("|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]")
            .append("[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}")
            .append("|[1-9][0-9]|[0-9])))")
            .append("(?:\\:\\d{1,5})?)") // plus option port number
            .append("(\\/(?:(?:[a-zA-Z0-9\\;\\/\\?\\:\\@\\&\\=\\#\\~")  // plus option query params
            .append("\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?")
            .append("(?:\\b|$)").toString();

    
    @Override
    public Navigation run() throws Exception {
        Validators v = new Validators(request);
        v.add("id", v.required(), v.longType());
        v.add("success", v.required());
        
        try {
            JSONObject json = new JSONObject(new JSONTokener(request.getReader()));
            
            requestScope("success", json.getBoolean("success"));
            
            if (asBoolean("success"))
            {
                v.add("url", v.required(), v.regexp(URL_PATTERN));
                requestScope("url", json.getString("app-url"));
            } else {
                v.add("reason", v.required());
                requestScope("reason", json.getString("reason"));
            }
        }
        catch (Exception e)
        {
            log.warning("Cannot parse JSON payload: " + e.getMessage());
        }
        
        
        if (! v.validate()) {
            Errors errors = v.getErrors();
            log.warning(String.format("Got an invalid set of input parameters: id=%s (%s), success=%s, url=%s (%s), reason=%s (%s)", asString("id"), errors.get("id"), asBoolean("success"), asString("url"), errors.get("url"), asString("reason"), errors.get("reason")));
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            return null;
        }
        
        log.info(String.format("App creation notification: id=%s, success=%s, url=%s, reason=%s", asString("id"), asBoolean("success"), asString("url"), asString("reason")));
        
        try {
            Application app = Datastore.get(ApplicationMeta.get(), Datastore.createKey(ApplicationMeta.get(), asLong("id")));
            
            if (asBoolean("success"))
                app.setDownloadUrl(asString("url"));
            //else
            //    app.setReason(asString("reason"));
            
            Datastore.put(app);

            response.setStatus(HttpURLConnection.HTTP_OK);
        }
        catch (EntityNotFoundRuntimeException e)
        {
            log.warning("App not found: " + asLong("id"));
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        catch (NumberFormatException e)
        {
            response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        }
        
        return null;
    }
    
}
