package com.appspot.eventorama.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Logout extends Composite {

    private static LogoutUiBinder uiBinder = GWT.create(LogoutUiBinder.class);

    interface LogoutUiBinder extends UiBinder<Widget, Logout> {
    }

    @UiField
    Anchor link;

    public Logout(String logoutUrl) {
        initWidget(uiBinder.createAndBindUi(this));
        link.setHref(logoutUrl);
    }
}
