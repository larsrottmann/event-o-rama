package com.appspot.eventorama.server.controller.notify;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.HttpURLConnection;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Before;
import org.junit.Test;

import com.appspot.eventorama.server.meta.ApplicationMeta;
import com.appspot.eventorama.shared.model.Application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class IndexControllerTest extends ControllerTestCase {

    Application app;
    
    
    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        app = new Application();
        Datastore.put(app);
    }

    @Test
    public void testNotifyApp() throws Exception {
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
        tester.request.setReader(new BufferedReader(new StringReader("")));
        tester.start("/notify/" + app.getKey().getId());
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }
    
    @Test
    public void testNotifyInvalidDownloadUrl() throws Exception {
        tester.request.setReader(new BufferedReader(new StringReader("{\"success\": true, \"app-url\": \"invalid_apk_url\"}")));
        tester.start("/notify/" + app.getKey().getId());
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

}
