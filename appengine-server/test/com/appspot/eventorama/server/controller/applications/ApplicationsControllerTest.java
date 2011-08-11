package com.appspot.eventorama.server.controller.applications;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

public class ApplicationsControllerTest extends ControllerTestCase {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalUserServiceTestConfig())
            .setEnvAuthDomain("MY_DOMAIN")
            .setEnvEmail("test@example.com")
            .setEnvIsLoggedIn(true);

    private Application app;
    

    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        helper.setUp();

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        app = new Application();
        app.setUser(user);
        app.setTitle("Test-Application");
        app.setStartDate(new Date(System.currentTimeMillis() - 86400 * 1000));            // yesterday
        app.setExpirationDate(new Date(System.currentTimeMillis() + 604800 * 1000));      // in one week
        Datastore.put(app);
    }
    

    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#tearDown()
     */
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        helper.tearDown();
    }



    @Test
    public void testGetApplication() throws Exception {
        tester.request.setMethod("get");
        tester.start("/applications/" + KeyFactory.keyToString(app.getKey()));
        ApplicationsController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONObject json = new JSONObject(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(Arrays.asList(JSONObject.getNames(json)), hasItems("title", "active", "start-date", "expiration-date"));
        assertThat(json.getLong("start-date"), is(app.getStartDate().getTime()));
        assertThat(json.getLong("expiration-date"), is(app.getExpirationDate().getTime()));
        assertThat(json.getBoolean("active"), is(app.isActive()));
        assertThat(json.getString("title"), is(app.getTitle()));
    }

    @Test
    public void testGetInvalidApplicationId() throws Exception {
        tester.request.setMethod("get");
        tester.start("/applications/123");
        ApplicationsController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testGetNonExistentApplication() throws Exception {
        tester.request.setMethod("get");
        tester.start("/applications/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666));
        ApplicationsController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

}
