package com.appspot.eventorama.client;

import java.util.List;

import com.appspot.eventorama.client.service.ApplicationsService;
import com.appspot.eventorama.client.service.ApplicationsServiceAsync;
import com.appspot.eventorama.client.service.LoginService;
import com.appspot.eventorama.client.service.LoginServiceAsync;
import com.appspot.eventorama.shared.model.Application;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Main implements EntryPoint {

    private final ApplicationsServiceAsync applicationsService = GWT.create(ApplicationsService.class);
    
    private LoginInfo loginInfo = null;
    
    public void onModuleLoad() {
        // Check login status using login service.
        LoginServiceAsync loginService = GWT.create(LoginService.class);
        loginService.login(
            GWT.getHostPageBaseURL(),
            new AsyncCallback<LoginInfo>() {
                public void onFailure(Throwable error) {
                }

                public void onSuccess(LoginInfo result) {
                    loginInfo = result;
                    if (loginInfo.isLoggedIn()) {
                        loadApplications();
                    } else {
                        loadLogin();
                    }
                }
            });
    }
    
    
    private void loadApplications() {
        applicationsService.getList(new AsyncCallback<List<Application>>() {

            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
                
            }

            public void onSuccess(List<Application> result) {
                // TODO Auto-generated method stub
                
            }
            
        });
        
        VerticalPanel logoutPanel = new VerticalPanel();
        Anchor signOutLink = new Anchor("Sign Out");
        signOutLink.setHref(loginInfo.getLogoutUrl());
        logoutPanel.add(signOutLink);
        RootPanel.get("appList").add(logoutPanel);
    }
    
    
    private void loadLogin() {
        // Assemble login panel.
        VerticalPanel loginPanel = new VerticalPanel();
        Label loginLabel = new Label("Please sign in to your Google Account to access the Event-O-Rama application.");
        Anchor signInLink = new Anchor("Sign In");

        signInLink.setHref(loginInfo.getLoginUrl());
        loginPanel.add(loginLabel);
        loginPanel.add(signInLink);
        RootPanel.get("appList").add(loginPanel);
    }
}