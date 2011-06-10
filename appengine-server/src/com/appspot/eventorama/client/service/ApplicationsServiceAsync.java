package com.appspot.eventorama.client.service;

import java.util.List;

import com.appspot.eventorama.shared.model.Application;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationsServiceAsync {

    void getList(AsyncCallback<List<Application>> callback);

}
