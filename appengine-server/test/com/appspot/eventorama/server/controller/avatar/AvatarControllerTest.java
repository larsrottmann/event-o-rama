package com.appspot.eventorama.server.controller.avatar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import javax.servlet.ServletInputStream;

import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;
import org.junit.Before;
import org.junit.Test;

import com.appspot.eventorama.shared.model.Application;
import com.appspot.eventorama.shared.model.Avatar;
import com.appspot.eventorama.shared.model.User;
import com.google.appengine.api.datastore.KeyFactory;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class AvatarControllerTest extends ControllerTestCase {

    private Application app;
    private User user;


    /* (non-Javadoc)
     * @see org.slim3.tester.ControllerTestCase#setUp()
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        app = new Application();
        Datastore.put(app);
        
        user = new User();
        user.setName("Boromir");
        user.getApplicationRef().setModel(app);
        Datastore.put(user);
    }

    @Test
    public void testCreateAvatar() throws Exception {
        tester.request.setMethod("post");
        tester.request.setContentType("image/jpeg");
        tester.request.setInputStream(new MockServletInputStream(this.getClass().getResourceAsStream("/com/appspot/eventorama/server/controller/avatar/sample.jpg")));

        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId() + "/avatar");
        AvatarController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_CREATED));
        assertThat(tester.response.containsHeader("location"), is(true));
    }
    
    @Test
    public void testCreateForNonExistentUser() throws Exception {
        tester.request.setMethod("post");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123/avatar");
        AvatarController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testGetAvatar() throws Exception {
        Avatar avatar = createAvatar(user);
        Datastore.put(avatar);

        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/" + user.getKey().getId() + "/avatar");
        AvatarController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_OK));
        assertThat(tester.response.getContentType(), is(avatar.getMimeType()));
        assertThat(tester.response.getContentLength(), is(avatar.getBytes().length));
    }

    @Test
    public void testGetForNonExistentUser() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.keyToString(app.getKey()) + "/users/123/avatar");
        AvatarController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }

    @Test
    public void testInvalidAppId() throws Exception {
        tester.request.setMethod("get");
        tester.start("/app/" + KeyFactory.createKeyString(Application.class.getSimpleName(), -666) + "/users/" + user.getKey().getId() + "/avatar");
        AvatarController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.response.getStatus(), is(HttpURLConnection.HTTP_NOT_FOUND));
    }


    private Avatar createAvatar(User user) throws IOException {
        Avatar avatar = new Avatar();
        avatar.getUserRef().setModel(user);
        avatar.setMimeType("image/jpeg");
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        InputStream in = this.getClass().getResourceAsStream("/com/appspot/eventorama/server/controller/avatar/sample.jpg");
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = in.read(buffer)))
            bout.write(buffer, 0, n);
        bout.flush();

        avatar.setBytes(bout.toByteArray());
        return avatar;
    }

    static final class MockServletInputStream extends ServletInputStream {

        /**
         * The original input stream.
         */
        protected InputStream inputStream;

        /**
         * Constructor.
         * 
         * @param inputStream
         *            the original input stream
         * @throws NullPointerException
         *             if the inputStream parameter is null
         */
        public MockServletInputStream(InputStream inputStream)
                throws NullPointerException {
            if (inputStream == null) {
                throw new NullPointerException(
                        "The inputStream parameter is null.");
            }
            this.inputStream = inputStream;
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

}
