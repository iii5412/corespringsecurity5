package com.security.corespringsecurity5.security.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/*
    AuthenticationDetails
    Authentication객체의 Details에 담기며 getDetails로 꺼내올수있다.
    parameter로 넘어온 값 등의 데이터를 추가로 인증시 사용 할 수 있다.
 */
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {
    public static final String SECRET_KEY_NAME = "secret_key";
    private String secretKey;

    public FormWebAuthenticationDetails(HttpServletRequest request){
        super(request);
        this.secretKey = request.getParameter(SECRET_KEY_NAME);
    }

    public String getSecretKey(){
        return this.secretKey;
    }



}
