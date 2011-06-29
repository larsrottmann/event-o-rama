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
import org.junit.Test;

import com.appspot.eventorama.server.meta.UserMeta;
import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.*;

public class UserControllerTest extends ControllerTestCase {

    @Test
    public void testCreateUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("post");
            tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Boromir\", \"device-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CREATED));
            assertThat(tester.response.containsHeader("location"), is(true));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testCreateUserMissingUserName() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("post");
            tester.request.setReader(new BufferedReader(new StringReader("{\"device-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testCreateUserMissingDeviceId() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("post");
            tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Boromir\"}")));

            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testCreateUserNameAlreadyTaken() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
        
        try
        {
            tester.request.setMethod("post");
            tester.request.setReader(new BufferedReader(new StringReader("{\"name\": \"Boromir\", \"device-id\": \"iw9eijd2rolrjo3jr0ufbbk888\"}")));

            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CONFLICT));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }

    @Test
    public void testGetUsers() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user1 = new User();
        user1.setName("Boromir");
        user1.getApplicationRef().setModel(app);
        User user2 = new User();
        user2.setName("Arwen");
        user2.getApplicationRef().setModel(app);
        Datastore.put(user1, user2);
        
        try
        {
            tester.request.setMethod("get");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
            assertThat(tester.response.getContentType().contains("application/json"), is(true));
            
            JSONArray json = new JSONArray(new JSONTokener(tester.response.getOutputAsString()));
            assertThat(json.length(), is(2));
            assertThat(json.opt(0), instanceOf(JSONObject.class));
        }
        finally
        {
            Datastore.delete(user1.getKey(), user2.getKey(), app.getKey());
        }
    }
    
    @Test
    public void testGetUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
        
        try
        {
            tester.request.setMethod("get");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
            assertThat(tester.response.getContentType().contains("application/json"), is(true));
            
            JSONObject json = new JSONObject(new JSONTokener(tester.response.getOutputAsString()));
            assertThat(Arrays.asList(JSONObject.getNames(json)), hasItems("id", "name", "device-id"));
            assertThat((String) json.get("name"), is("Boromir"));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }

    @Test
    public void testGetNonExistentUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("get");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testUpdateUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);

        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829}")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
            
            User updatedUser = Datastore.get(UserMeta.get(), user.getKey());
            assertThat(updatedUser.getLocationUpdated(), is(new Date(1309350829)));
            assertThat(updatedUser.getLocation().getLatitude(), is(6.213211f));
            assertThat(updatedUser.getLocation().getLongitude(), is(51.4344453f));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }

    @Test
    public void testUpdateUserShouldSetLocationUpdateIfMissing() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);

        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211}")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
            
            User updatedUser = Datastore.get(UserMeta.get(), user.getKey());
            assertThat(updatedUser.getLocationUpdated(), is(notNullValue()));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }

    @Test
    public void testUpdateNonExistentUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"lat\": 6.213211, \"location-update\": 1309350829}")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }

    @Test
    public void testUpdateUserMissingLonLocation() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
        
        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("{\"lat\": 6.213211, \"location-update\": 1309350829}")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }

    @Test
    public void testUpdateUserMissingLatLocation() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
        
        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("{\"lon\": 51.4344453, \"location-update\": 1309350829}")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }
    
    @Test
    public void testUpdateUserInvalidJson() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        User user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
        
        try
        {
            tester.request.setMethod("put");
            tester.request.setReader(new BufferedReader(new StringReader("\"lon\": 51.4344453, \"location-update\": 1309350829")));
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId());
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_BAD_REQUEST));
        }
        finally
        {
            Datastore.delete(user.getKey(), app.getKey());
        }
    }
    
    @Test
    public void testInvalidAppId() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666) + "/users");
        UserController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

}
