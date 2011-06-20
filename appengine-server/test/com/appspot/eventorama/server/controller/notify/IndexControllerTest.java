package com.appspot.eventorama.server.controller.notify;

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
            final String downloadUrl = "some_apk_download_url";
            tester.param("url", downloadUrl);
            tester.start("/notify/" + app.getKey().getId());
            IndexController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.asLong("id"), is(app.getKey().getId()));
            assertThat(tester.response.getStatus(), is(200));

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
        tester.param("url", "some_apk_download_url");
        tester.start("/notify/-666");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(404));
    }
    
    @Test
    public void testNotifyInvalidAppId() throws Exception {
        tester.param("url", "some_apk_download_url");
        tester.start("/notify/invalid");
        IndexController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(400));
    }

    @Test
    public void testNotifyNonExistentDownloadUrl() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.start("/notify/" + app.getKey().getId());
            IndexController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(400));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }
    

}
