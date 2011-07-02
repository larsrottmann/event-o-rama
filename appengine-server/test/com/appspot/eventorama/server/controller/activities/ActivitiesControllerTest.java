package com.appspot.eventorama.server.controller.activities;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Date;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;

import com.appspot.eventorama.server.controller.activities.ActivitiesController;
import com.appspot.eventorama.shared.model.Activity;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class ActivitiesControllerTest extends ControllerTestCase {

    private Application app;
    private User user;
    private Activity activity1, activity2;
    

    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        app = new Application();
        app.setStartDate(new Date(System.currentTimeMillis() - 86400 * 1000));            // yesterday
        app.setExpirationDate(new Date(System.currentTimeMillis() + 604800 * 1000));      // in one week
        Datastore.put(app);
        
        user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);

        activity1 = new Activity();
        activity1.setText("Will soon be there.");
        activity1.setTimestamp(new Date(System.currentTimeMillis()));
        activity1.getApplicationRef().setModel(app);
        activity1.getUserRef().setModel(user);
        activity2 = new Activity();
        activity2.setText("Arrived at venue.");
        activity2.setTimestamp(new Date(System.currentTimeMillis() + 3600 * 1000));
        activity2.getApplicationRef().setModel(app);
        activity2.getUserRef().setModel(user);
        Datastore.put(activity1, activity2);
    }
    
    @Test
    public void testCreateActivity() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CREATED));
        assertThat(tester.response.containsHeader("location"), is(true));
    }

    @Test
    public void testCreateActivityMissingUserId() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateActivityMissingTimestamp() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateActivityMissingType() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateActivityMissingText() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + "}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateActivityInvalidJson() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testShouldNotCreateActivityForInactiveApplication() throws Exception {
        app.setActive(false);
        Datastore.put(app);
        
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_ACCEPTABLE));
    }

    @Test
    public void testShouldNotCreateActivityBeforeApplicationStarts() throws Exception {
        app.setStartDate(new Date(System.currentTimeMillis() + 86400 * 1000));            // tomorrow
        app.setExpirationDate(new Date(System.currentTimeMillis() + 604800 * 1000));      // in one week
        Datastore.put(app);

        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_ACCEPTABLE));
    }

    @Test
    public void testShouldNotCreateActivityAfterApplicationExpired() throws Exception {
        app.setStartDate(new Date(System.currentTimeMillis() - 604800 * 1000));           // one week ago 
        app.setExpirationDate(new Date(System.currentTimeMillis() - 86400 * 1000));       // yesterday
        Datastore.put(app);

        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_ACCEPTABLE));
    }

    @Test
    public void testShouldNotCreateActivityForNonAppUser() throws Exception {
        user = new User();
        user.setName("Arwen");
        Datastore.put(user);

        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"timestamp\": " + System.currentTimeMillis() + ", \"type\": 1, \"user-id\": " + user.getKey().getId() + ", \"text\": \"Will be late.\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testGetActivities() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONArray jsonArray = new JSONArray(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(jsonArray.length(), is(2));
        assertThat(jsonArray.opt(0), instanceOf(JSONObject.class));
        assertThat(((JSONObject) jsonArray.opt(0)).getString("text"), is(activity1.getText()));
    }

    @Test
    public void testGetActivitiesFilteredByTimestamp() throws Exception {
        tester.request.setMethod("get");
        tester.request.setParameter("since", Long.toString(System.currentTimeMillis() + 1800 * 1000));   // should only return latest)
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONArray jsonArray = new JSONArray(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(jsonArray.length(), is(1));
        assertThat(jsonArray.opt(0), instanceOf(JSONObject.class));
        assertThat(((JSONObject) jsonArray.opt(0)).getString("text"), is(activity2.getText()));
    }
    
    @Test
    public void testGetActivity() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities/" + activity1.getKey().getId());
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONObject json = new JSONObject(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(Arrays.asList(JSONObject.getNames(json)), hasItems("timestamp", "type", "user-id", "text"));
        assertThat(json.getLong("timestamp"), is(activity1.getTimestamp().getTime()));
        assertThat(json.getLong("user-id"), is(activity1.getUserRef().getKey().getId()));
        assertThat(json.getString("text"), is(activity1.getText()));
    }

    @Test
    public void testGetNonExistentActivity() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/activities/123");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testInvalidAppId() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666) + "/activities");
        ActivitiesController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }


}
