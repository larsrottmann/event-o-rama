package com.appspot.eventorama.server.controller.users;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.HttpURLConnection;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Test;

import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
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
            Datastore.delete(app.getKey());
            Datastore.delete(user.getKey());
        }
    }

    

    @Test
    public void testGetUsers() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("get");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        }
        finally
        {
            Datastore.delete(app.getKey());
        }
    }
    
    @Test
    public void testGetUser() throws Exception {
        Application app = new Application();
        Datastore.put(app);
        
        try
        {
            tester.request.setMethod("get");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
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
        
        try
        {
            tester.request.setMethod("put");
            tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123");
            UserController controller = tester.getController();
            assertThat(controller, is(notNullValue()));
            assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        }
        finally
        {
            Datastore.delete(app.getKey());
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
