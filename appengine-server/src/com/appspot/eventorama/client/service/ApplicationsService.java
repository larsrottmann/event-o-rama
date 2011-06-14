package com.appspot.eventorama.client.service;

import java.util.Date;
import java.util.List;

import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service.s3gwt")
public interface ApplicationsService extends RemoteService {

    public List<Application> getList() throws NotLoggedInException;
    
    public void create(String title, Date startDate, Date expirationDate) throws NotLoggedInException;
    
    public void delete(Key appKey) throws NotLoggedInException;
}
