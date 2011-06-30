package com.appspot.eventorama.shared.model;

import java.util.Date;

import org.slim3.datastore.Datastore;
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
        
        // manually deactivate the event
        model.setActive(false);
        assertThat(model.isActive(), is(false));
    }
    
    @Test
    public void testActivitiesRelation() throws Exception {
        Activity activity = new Activity();
        activity.setText("Arrived at venue");
        activity.getApplicationRef().setModel(model);
        
        Datastore.put(model, activity);
        
        assertThat(model.getActivityListRef().getModelList().size(), is(1));
        assertThat(model.getActivityListRef().getModelList().get(0), equalTo(activity));
    }

    @Test
    public void testActivitiesRelationSortOrder() throws Exception {
        Activity activity1 = new Activity();
        activity1.setTimestamp(new Date(System.currentTimeMillis()));
        activity1.getApplicationRef().setModel(model);
        
        Activity activity2 = new Activity();
        activity2.setTimestamp(new Date(System.currentTimeMillis() + 86400));
        activity2.getApplicationRef().setModel(model);
        
        Datastore.put(model, activity1, activity2);
        
        assertThat(model.getActivityListRef().getModelList().size(), is(2));
        // check that activities are sorted in descending order according to their timestamp
        assertThat(model.getActivityListRef().getModelList().get(0), equalTo(activity2));
        assertThat(model.getActivityListRef().getModelList().get(1), equalTo(activity1));
    }

}
