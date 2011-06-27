package com.appspot.eventorama.client.service;

import java.util.List;

import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service.s3gwt")
public interface ApplicationsService extends RemoteService {

    public List<Application> getList() throws NotLoggedInException;
    
    public Key create(Application app) throws NotLoggedInException;
    
    public void delete(Key appKey) throws NotLoggedInException;
    
    public Application get(Key appKey) throws NotLoggedInException;
}
