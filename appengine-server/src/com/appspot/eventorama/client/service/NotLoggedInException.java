package com.appspot.eventorama.client.service;

import java.io.Serializable;

public class NotLoggedInException extends Exception implements Serializable {

    private static final long serialVersionUID = 4020715954730377342L;

    public NotLoggedInException() {
        super();
    }

    public NotLoggedInException(String message) {
        super(message);
    }

}
