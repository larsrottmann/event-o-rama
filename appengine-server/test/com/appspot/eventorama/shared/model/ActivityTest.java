package com.appspot.eventorama.shared.model;

import org.slim3.tester.AppEngineTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ActivityTest extends AppEngineTestCase {

    private Activity model = new Activity();

    @Test
    public void test() throws Exception {
        assertThat(model, is(notNullValue()));
    }
}
