package com.appspot.eventorama.client;

import com.appspot.eventorama.client.service.LoginService;
import com.appspot.eventorama.client.service.LoginServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint {

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
                        RootPanel.get("appList").add(new AppList());
                        RootPanel.get("account").add(new Logout(loginInfo.getLogoutUrl()));
                    } else {
                        RootPanel.get("account").add(new Login(loginInfo.getLoginUrl()));
                    }
                }
            });
    }
    

}