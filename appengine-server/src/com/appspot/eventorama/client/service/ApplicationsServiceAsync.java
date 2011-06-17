package com.appspot.eventorama.client.service;

import java.util.Date;
import java.util.List;

import com.appspot.eventorama.shared.model.Application;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationsServiceAsync {

    void getList(AsyncCallback<List<Application>> callback);

    void create(String title, Date startDate, Date expirationDate, AsyncCallback<Void> callback);

    void delete(Key appKey, AsyncCallback<Void> callback);

    void get(Key appKey, AsyncCallback<Application> callback);

}
