package com.appspot.eventorama.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.eventorama.server.ServerConfig;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.google.appengine.repackaged.com.google.common.util.Base64DecoderException;

public class AuthFilter implements Filter {

    private FilterConfig filterConfig;

    public void destroy() {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"Secure Area\"");
        } else {
            String[] auth_parts = authHeader.split(" ");
            String[] user_pass_parts;
            try {
                user_pass_parts = (new String(Base64.decode(auth_parts[1]))).split(":");
            } catch (Base64DecoderException e) {
                user_pass_parts = new String[] { null, null };
            }
            String user_arg = user_pass_parts[0];
            String pass_arg = user_pass_parts[1];
            
            if (!(user_arg.equals(ServerConfig.getInstance().getProperty("auth.user")) && pass_arg.equals(ServerConfig.getInstance().getProperty("auth.password")))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "Basic realm=\"Secure Area\"");
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

}
