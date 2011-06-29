package com.appspot.eventorama.server.controller.download;

import java.net.HttpURLConnection;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.appspot.eventorama.server.controller.download.IndexController;
import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IndexControllerTest extends ControllerTestCase {

    @Test
    public void testDownloadApp() throws Exception {
        Application app = new Application();
        app.setDownloadUrl("http://some.host.com/download/link");
        Datastore.put(app);
        
        tester.start("/download/" + KeyFactory.keyToString(app.getKey()));
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.asKey("id"), is(app.getKey()));
        assertThat(tester.isRedirect(), is(true));
        assertThat(tester.getDestinationPath(), is(app.getDownloadUrl()));
    }

    @Test
    public void testDownloadAppInProgress() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        tester.start("/download/" + KeyFactory.keyToString(app.getKey()));
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.asKey("id"), is(app.getKey()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_ACCEPTED));
    }

    @Test
    public void testDownloadNonExistentAppId() throws Exception {
        tester.start("/download/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666));
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }
    
    @Test
    public void testDownloadInvalidAppId() throws Exception {
        tester.start("/download/");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

}
