package com.appspot.eventorama.shared.model;

import java.util.Date;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ApplicationTest extends AppEngineTestCase {

    private Application model = new Application();

    @Test
    public void testIsActive() throws Exception {
        assertThat(model, is(notNullValue()));
        assertThat(model.isActive(), is(false));
        
        // only one date doesn't make the event active
        model.setStartDate(new Date(System.currentTimeMillis()));
        model.setExpirationDate(null);
        assertThat(model.isActive(), is(false));
        
        // only one date doesn't make the event active
        model.setStartDate(null);
        model.setExpirationDate(new Date(System.currentTimeMillis()));
        assertThat(model.isActive(), is(false));
        
        // start date in the future doesn't make the event active
        model.setStartDate(new Date(System.currentTimeMillis() + 86400));
        model.setExpirationDate(new Date(System.currentTimeMillis() + 2*86400));
        assertThat(model.isActive(), is(false));

        // end date in the past doesn't make the event active
        model.setStartDate(new Date(System.currentTimeMillis() - 2*86400));
        model.setExpirationDate(new Date(System.currentTimeMillis() - 86400));
        assertThat(model.isActive(), is(false));
        
        // start date in the past and end date in the future makes an active event
        model.setStartDate(new Date(System.currentTimeMillis() - 86400));
        model.setExpirationDate(new Date(System.currentTimeMillis() + 86400));
        assertThat(model.isActive(), is(true));
    }
}
