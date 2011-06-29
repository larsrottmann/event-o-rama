package com.appspot.eventorama.client.service;

import com.appspot.eventorama.client.model.LoginInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service.s3gwt")
public interface LoginService extends RemoteService {

    public LoginInfo login(String requestUri);
    
}
