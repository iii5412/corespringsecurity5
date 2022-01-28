package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.handler.CustomAuthenticationFailureHandler;
import com.security.corespringsecurity5.security.handler.CustomAuthenticationSuccessHandler;
import com.security.corespringsecurity5.security.common.FormAuthenticationDetailsSource;
import com.security.corespringsecurity5.security.provider.CustomAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@EnableWebSecurity
@Order(1)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //정적 자원 ignore
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //접근 권한 처리
        http.authorizeRequests((authorizeRequest) -> {
            /*루트페이지, 사용자 가입화면, 로그인화면은 인증 없이 접근 가능
            로그인 실패시 /login?error=ture 로 접근하는데, Security는 이것도 보두 다른 URL로 보기때문에
            /login* 으로 처리.
            */
            authorizeRequest.antMatchers("/", "/users", "/user/login/**", "/login*").permitAll();

            // /myPage는 USER 권한만 접근 가능
            authorizeRequest.antMatchers("/mypage").hasRole("USER");
            // /messages는 MANAGER만 접근 가능
            authorizeRequest.antMatchers("/messages").hasRole("MANAGER");
            // /config는 ADMIN만 접근 가능
            authorizeRequest.antMatchers("/config").hasRole("ADMIN");

            //그 외 모든 요청은 인증된 사용자만 접근 가능.
            authorizeRequest.anyRequest().authenticated();
        });

        //인증 처리
        //로그인 처리
        http.formLogin((formLogin) -> {
            //로그인 페이지 URL 설정
           formLogin.loginPage("/login");
           /*
           로그인 처리 URL 설정 - 따로 로그인을 처리한다기보다는
           POST로 해당 URL로 접근시 Security가 제공해주는 authentication인증방식을 호출해준다.
            */
           formLogin.loginProcessingUrl("/login_proc");
           //AuthenticationDetails 등록(secretKey 사용)
           formLogin.authenticationDetailsSource(authenticationDetailsSource());
           //successHandler
            formLogin.successHandler(authenticationSuccessHandler());
            //Failure Handler
            formLogin.failureHandler(authenticationFailureHandler());
        });

        //ExceptionHandle
        http.exceptionHandling((httpSecurityExceptionHandlingConfigurer) -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                //접근 예외 발생시 redirect
                String deniedUrl = "/denied?exception=" + accessDeniedException.getMessage();
                response.sendRedirect(deniedUrl);
            });
        });
    }

    @Bean
    public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new FormAuthenticationDetailsSource();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
