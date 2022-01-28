package com.security.corespringsecurity5.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errMessage = "Login Fail";

        if(exception instanceof InsufficientAuthenticationException){
            errMessage = "Invalid Secret key";
        }else if(exception instanceof BadCredentialsException){
            errMessage = "Invalid Username or Password";
        }

        setDefaultFailureUrl("/login?error=true&exception=" + errMessage);
        //부모에게 나머지 처리 위임
        super.onAuthenticationFailure(request, response, exception);

    }
}
