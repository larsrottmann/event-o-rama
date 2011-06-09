package com.appspot.eventorama.client.service;

import com.appspot.eventorama.client.LoginInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

    void login(String requestUri, AsyncCallback<LoginInfo> callback);

}
