package com.appspot.eventorama.client.service;

import java.util.List;

import com.appspot.eventorama.shared.model.Application;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service.s3gwt")
public interface ApplicationsService extends RemoteService {

    public List<Application> getList() throws NotLoggedInException;
}
