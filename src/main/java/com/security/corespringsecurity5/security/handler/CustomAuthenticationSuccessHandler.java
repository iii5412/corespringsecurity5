package com.security.corespringsecurity5.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    //인증 전 리퀘스트의 정보를 담고있는 RequestCache
    private RequestCache requestCache = new HttpSessionRequestCache();

    //이전 요청정보 URL 등 URL로 Redirect하기위한 Redirect전략 객체
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //DefaultUrl 설정
        setDefaultTargetUrl("/");

        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        /**
         * savedReqeust는 객체가 null일 수도있다.
         * 특정 자원에 접근을 시도 후 인증되지않아 실패하여 Login화면으로 들어온경우 이전 URL이 있지만
         * 바로 로그인 화면으로 접근한경우 이전 Request가 없기 때문이다.
         */
        if(savedRequest != null) {
            redirectStrategy.sendRedirect(request,response, savedRequest.getRedirectUrl());
        }else{
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
