package com.appspot.eventorama.server.service;

import com.appspot.eventorama.client.LoginInfo;
import com.appspot.eventorama.client.service.LoginService;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginServiceImpl implements LoginService {

    public LoginInfo login(String requestUri) {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        LoginInfo loginInfo = new LoginInfo();

        if (user != null) {
            loginInfo.setLoggedIn(true);
            loginInfo.setEmailAddress(user.getEmail());
            loginInfo.setNickname(user.getNickname());
            loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
        } else {
            loginInfo.setLoggedIn(false);
            loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
        }
        return loginInfo;
    }

}
