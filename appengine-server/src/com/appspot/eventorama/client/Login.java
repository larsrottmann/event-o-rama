package com.appspot.eventorama.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Login extends Composite {

    private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class);

    interface LoginUiBinder extends UiBinder<Widget, Login> {
    }

    @UiField
    Anchor link;

    public Login(String loginUrl) {
        initWidget(uiBinder.createAndBindUi(this));
        link.setHref(loginUrl);
    }
}
