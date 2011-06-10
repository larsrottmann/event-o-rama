package com.appspot.eventorama.server.service;

import org.slim3.tester.ServletTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ApplicationsServiceImplTest extends ServletTestCase {

    private ApplicationsServiceImpl service = new ApplicationsServiceImpl();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
