package com.appspot.eventorama.server.controller;

import org.slim3.controller.router.RouterImpl;

public class AppRouter extends RouterImpl {

    public AppRouter() {
        // App-Maker completed APK generation
        addRouting(
            "/notify/{id}",
            "/notify/index?id={id}");
        
        // APK download URL
        addRouting(
            "/download/{id}",
            "/download/index?id={id}");
        
        
        // Android Client
        
        // User Management: POST / GET 
        addRouting(
            "/app/{app_id}/users",
            "/users/users?app_id={app_id}");
        // User Management: PUT
        addRouting(
            "/app/{app_id}/users/{user_id}",
            "/users/users?app_id={app_id}&user_id={user_id}");
        
        // Activities Management: POST / GET
        addRouting(
            "/app/{app_id}/activities",
            "/activities/activities?app_id={app_id}");

    }
}