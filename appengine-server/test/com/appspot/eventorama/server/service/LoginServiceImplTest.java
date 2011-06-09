package com.appspot.eventorama.server.service;

import org.slim3.tester.ServletTestCase;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class LoginServiceImplTest extends ServletTestCase {

    private LoginServiceImpl service = new LoginServiceImpl();

    @Test
    public void test() throws Exception {
        assertThat(service, is(notNullValue()));
    }
}
