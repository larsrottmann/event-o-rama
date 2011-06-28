package com.appspot.eventorama.client;

import com.appspot.eventorama.client.service.LoginService;
import com.appspot.eventorama.client.service.LoginServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint {

    private static final int TRANSIENT_MESSAGE_HIDE_DELAY = 5000;
    
    public static RootPanel sMessagePanel = null;
    
    private LoginServiceAsync loginService = GWT.create(LoginService.class);
    private LoginInfo loginInfo = null;

    
    public void onModuleLoad() {
        showMessage("Loading...", false);

        loginService.login(
            GWT.getHostPageBaseURL(),
            new AsyncCallback<LoginInfo>() {
                public void onFailure(Throwable error) {
                    hideMessage();
                    Window.alert(error.getMessage());
                }

                public void onSuccess(LoginInfo result) {
                    hideMessage();
                    loginInfo = result;
                    if (loginInfo.isLoggedIn()) {
                        RootPanel.get("appList").add(new AppList());
                        RootPanel.get("account").add(new Logout(loginInfo.getLogoutUrl()));
                    } else {
                        RootPanel.get("account").add(new Login(loginInfo.getLoginUrl()));
                    }
                }
            }
        );
    }
    
    
    public static void showMessage(String message, boolean isTransient) {
        if (sMessagePanel == null) {
            sMessagePanel = RootPanel.get("messagePanel");
        }

        sMessagePanel.setVisible(true);
        sMessagePanel.getElement().setInnerText(message);
        if (isTransient) {
            new Timer() {
                @Override
                public void run() {
                    sMessagePanel.setVisible(false);
                }
            }.schedule(TRANSIENT_MESSAGE_HIDE_DELAY);
        }
    }

    public static void hideMessage() {
        sMessagePanel.setVisible(false);
    }
}
