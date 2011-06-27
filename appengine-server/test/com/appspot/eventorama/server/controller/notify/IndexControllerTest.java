package com.appspot.eventorama.server.controller.notify;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.HttpURLConnection;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IndexControllerTest extends ControllerTestCase {


    @Test
    public void testNotifyApp() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            final String downloadUrl = "http://127.0.0.1/download/apk";
            tester.request.setReader(new BufferedReader(new StringReader("{\"success\": true, \"app-url\": \"" + downloadUrl + "\"}")));
            tester.start("/notify/" + app.getKey().getId());
            IndexController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.asLong("id"), is(app.getKey().getId()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));

            app = Datastore.get(ApplicationMeta.get(), app.getKey());
            assertThat(app.getDownloadUrl(), is(notNullValue()));
            assertThat(app.getDownloadUrl(), is(downloadUrl));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testNotifyNonExistentAppId() throws Exception {
        tester.request.setReader(new BufferedReader(new StringReader("{\"success\": true, \"app-url\": \"http://127.0.0.1/download/apk\"}")));
        tester.start("/notify/-666");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }
    
    @Test
    public void testNotifyInvalidAppId() throws Exception {
        tester.request.setReader(new BufferedReader(new StringReader("{\"success\": true, \"app-url\": \"http://127.0.0.1/download/apk\"}")));
        tester.start("/notify/invalid");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testNotifyNonExistentDownloadUrl() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setReader(new BufferedReader(new StringReader("")));
            tester.start("/notify/" + app.getKey().getId());
            IndexController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }
    
    @Test
    public void testNotifyInvalidDownloadUrl() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setReader(new BufferedReader(new StringReader("{\"success\": true, \"app-url\": \"invalid_apk_url\"}")));
            tester.start("/notify/" + app.getKey().getId());
            IndexController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

}
