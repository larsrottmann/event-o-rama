package com.appspot.eventorama.server.controller.users;

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

import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class UsersControllerTest extends ControllerTestCase {

    private Application app;
    private User user1, user2;
    
    
    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        app = new Application();
        Datastore.put(app);
        
        user1 = new User();
        user1.setName("Boromir");
        user1.getApplicationRef().setModel(app);
        user2 = new User();
        user2.setName("Arwen");
        user2.getApplicationRef().setModel(app);
        user2.setRegistrationId("iw9eijd2rolrjo3jr0ufbbk888");
        user2.setLocation(new GeoPt(6.213211f, 51.4344453f));
        user2.setLocationUpdated(new Date(1309350829));
        user2.setAccuracy(4.3f);
        Datastore.put(user1, user2);
    }

    
    @Test
    public void testCreateUser() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Legolas\", \"registration-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CREATED));
        assertThat(tester.response.containsHeader("location"), is(true));
    }

    @Test
    public void testCreateUserMissingUserName() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"registration-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateUserMissingRegistrationId() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Boromir\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testCreateUserInvalidJson() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Legolas\", \"registration-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"")));
        
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }
    
    @Test
    public void testCreateUserNameAlreadyTaken() throws Exception {
        tester.request.setMethod("post");
        tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Boromir\", \"registration-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CONFLICT));
    }

    @Test
    public void testGetUsers() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONArray jsonArray = new JSONArray(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(jsonArray.length(), is(2));
        assertThat(jsonArray.opt(0), instanceOf(JSONObject.class));
    }
    
    @Test
    public void testGetUser() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONObject json = new JSONObject(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(Arrays.asList(JSONObject.getNames(json)), hasItems("id", "name", "registration-id"));
        assertThat(json.getString("name"), is(user1.getName()));
    }

    @Test
    public void testGetUserWithLocationInfo() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user2.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType().contains("application/json"), is(true));
        
        JSONObject json = new JSONObject(new JSONTokener(tester.response.getOutputAsString()));
        assertThat(Arrays.asList(JSONObject.getNames(json)), hasItems("id", "name", "registration-id", "lon", "lat", "location-update", "accuracy"));
        assertThat(json.getString("name"), is(user2.getName()));
        assertThat(json.getString("registration-id"), is(user2.getRegistrationId()));
        assertThat(json.getLong("location-update"), is(user2.getLocationUpdated().getTime()));
        assertThat(json.getDouble("lon"), is(notNullValue()));
        assertThat(json.getDouble("lat"), is(notNullValue()));
        assertThat(json.getDouble("accuracy"), is(notNullValue()));
    }

    @Test
    public void testGetNonExistentUser() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testUpdateUser() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829, \"accuracy\": 4.3}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        
        User updatedUser = Datastore.get(UserMeta.get(), user1.getKey());
        assertThat(updatedUser.getLocationUpdated(), is(new Date(1309350829)));
        assertThat(updatedUser.getLocation().getLatitude(), is(6.213211f));
        assertThat(updatedUser.getLocation().getLongitude(), is(51.4344453f));
        assertThat(updatedUser.getAccuracy(), is(4.3f));
    }

    @Test
    public void testUpdateUserShouldSetLocationUpdateIfMissing() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"accuracy\": 4.3}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        
        User updatedUser = Datastore.get(UserMeta.get(), user1.getKey());
        assertThat(updatedUser.getLocationUpdated(), is(notNullValue()));
    }

    @Test
    public void testUpdateNonExistentUser() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829, \"accuracy\": 4.3}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testUpdateUserMissingLonLocation() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lat\": 6.213211, \"location-update\": 1309350829, \"accuracy\": 4.3}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }

    @Test
    public void testUpdateUserMissingLatLocation() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"location-update\": 1309350829, \"accuracy\": 4.3}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }
    
    @Test
    public void testUpdateUserMissingAccuracy() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829}")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }
    
    @Test
    public void testUpdateUserInvalidJson() throws Exception {
        tester.request.setMethod("put");
        tester.request.setReader(new BufferedReader(new StringReader("\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829, \"accuracy\": 4.3")));
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user1.getKey().getId());
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
    }
    
    @Test
    public void testInvalidAppId() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666) + "/users");
        UsersController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

}
