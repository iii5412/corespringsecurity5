package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.common.AjaxLoginAuthenticationEntryPoint;
import com.security.corespringsecurity5.security.handler.AjaxAccessDeniedHandler;
import com.security.corespringsecurity5.security.handler.AjaxAuthenticationFailureHandler;
import com.security.corespringsecurity5.security.handler.AjaxAuthenticationSuccessHandler;
import com.security.corespringsecurity5.security.provider.AjaxAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableWebSecurity
//@Order(0)
@RequiredArgsConstructor
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/api/**");
        http.authorizeRequests((authorizeRequest) -> {
//            authorizeRequest.antMatchers("/api/**");
            authorizeRequest.antMatchers("/api/login").permitAll();
            authorizeRequest.antMatchers("/api/messages").hasRole("MANAGER");
            authorizeRequest.anyRequest().authenticated();
        });

        http.exceptionHandling((customizer) -> {
            customizer.authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint());
            customizer.accessDeniedHandler(new AjaxAccessDeniedHandler());
        });

        customConfigurerAjax(http);
    }
    //    @Bean

    /**
     * SpringSecurity의 설정 객체들은 굳이 빈으로 생성할 필요가 없다.
     * 모두 초기화 시 생성되고 나서는 이 객체들을 참조해야 할 경우가 거의 없기 때문이다.
     * 그래서 Spring Security는 HttpSecurity에 있는 SharedObjects를 가지고 여기에 여러 객체들을 넣어두고 참조하는 식으로 운용한다. 즉 빈이 아니다.
     * 다만 빈으로 생성하는 이유는 여러 위치에서 DI받기를 위함이다.
     */
    //Custom DSLs(Domain Specific Language)
    private void customConfigurerAjax(HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>())
                .setSuccessHandlerAjax(new AjaxAuthenticationSuccessHandler())
                .setFailureHandlerAjax(new AjaxAuthenticationFailureHandler())
                .setAuthenticatioManager(super.authenticationManagerBean())
                .loginProcessingUrl("/api/login");
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(this.customUserDetailsService, this.passwordEncoder);
    }

}
