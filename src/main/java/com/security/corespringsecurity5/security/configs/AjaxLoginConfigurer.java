package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.filter.AjaxLoginProcessingFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Slf4j
public class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>> extends AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> {
    
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private AuthenticationManager authenticationManager;

    public AjaxLoginConfigurer() {
        super(new AjaxLoginProcessingFilter(), null);
    }

    @Override
    public void init(H http) throws Exception {
        log.info("AjaxLoginConfigurer :: init 실행");
        super.init(http);
    }

    @Override
    public void configure(H http) throws Exception {
        log.info("AjaxLoginConfigurer :: configure 실행");
        //authenticationManager가 null이면
        if(authenticationManager == null){
            //Http Security 객체의 공유 객체 중 AuthenticationManager타입의 객체를 가져온다.
            authenticationManager = http.getSharedObject(AuthenticationManager.class);
        }

        //AuthenticationManager 등로
        getAuthenticationFilter().setAuthenticationManager(authenticationManager);
        //AuthenticationSuccessHandler 적용
        getAuthenticationFilter().setAuthenticationSuccessHandler(successHandler);
        //AuthenticationFailureHandler 적용
        getAuthenticationFilter().setAuthenticationFailureHandler(failureHandler);

        //SessionAuthentication strategy적용
        final SessionAuthenticationStrategy sessionAuthenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
        if(sessionAuthenticationStrategy != null){
            log.info("sessionAuthenticationStrategy = {}", sessionAuthenticationStrategy.getClass().getSimpleName());
            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        //RememberMeService 적용
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if(rememberMeServices != null){
            log.info("rememberMeServices = {}", rememberMeServices.getClass().getSimpleName());
            getAuthenticationFilter().setRememberMeServices(rememberMeServices);
        }

        //HttpSecurity 공유객체에 AjaxLoginProcessingFilter타입의 인증 필터 등록
        http.setSharedObject(AjaxLoginProcessingFilter.class, getAuthenticationFilter());
        //Filter리스트 중 UsernamePasswordAuthenticationFilter 보다 앞에 등록
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
    
    public AjaxLoginConfigurer<H> setSuccessHandlerAjax(AuthenticationSuccessHandler successHandler){
        log.info("AjaxLoginConfigurer :: setSuccessHandlerAjax 실행({})", successHandler.getClass().getSimpleName());
        this.successHandler = successHandler;
        return this;
    }
    
    public AjaxLoginConfigurer<H> setFailureHandlerAjax(AuthenticationFailureHandler failureHandler){
        log.info("AjaxLoginConfigurer :: setFailureHandlerAjax 실행({})", failureHandler.getClass().getSimpleName());
        this.failureHandler = failureHandler;
        return this;
    }
    
    public AjaxLoginConfigurer<H> setAuthenticatioManager(AuthenticationManager authenticationManager) {
        log.info("AjaxLoginConfigurer :: setAuthenticationManager 실행({})", failureHandler.getClass().getSimpleName());
        this.authenticationManager = authenticationManager;
        return this;
    }

    /**
     * Login 화면 Url 셋팅
     * @param loginProcessingUrl
     * @return
     */
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        log.info("loginProcessingUrl = {}", loginProcessingUrl);
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
