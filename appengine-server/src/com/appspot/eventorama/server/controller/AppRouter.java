package com.appspot.eventorama.server.controller;

import org.slim3.controller.router.RouterImpl;

public class AppRouter extends RouterImpl {

    public AppRouter() {
        addRouting(
            "/notify/{id}",
            "/notify/index?id={id}");
    }
}