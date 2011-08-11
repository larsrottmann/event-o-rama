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
        
        // Application Data: GET
        addRouting(
            "/applications/{id}",
            "/applications/applications?id={id}");
        
        // User Management: POST / GET (list)
        addRouting(
            "/app/{app_id}/users",
            "/users/users?app_id={app_id}");
        // User Management: PUT / GET (single)
        addRouting(
            "/app/{app_id}/users/{user_id}",
            "/users/users?app_id={app_id}&user_id={user_id}");
        // User Profile Pic Management: POST / GET (single)
        addRouting(
            "/app/{app_id}/users/{user_id}/avatar",
            "/avatar/avatar?app_id={app_id}&user_id={user_id}");
        
        // Activities Management: POST / GET (list)
        addRouting(
            "/app/{app_id}/activities",
            "/activities/activities?app_id={app_id}");
        // Activities Management: GET (single)
        addRouting(
            "/app/{app_id}/activities/{activity_id}",
            "/activities/activities?app_id={app_id}&activity_id={activity_id}");

    }
}