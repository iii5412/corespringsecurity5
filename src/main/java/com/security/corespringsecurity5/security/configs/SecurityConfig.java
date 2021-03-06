package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.common.FormAuthenticationDetailsSource;
import com.security.corespringsecurity5.security.factory.UrlResourcesMapFactoryBean;
import com.security.corespringsecurity5.security.filter.PermitAllFilter;
import com.security.corespringsecurity5.security.handler.CustomAuthenticationFailureHandler;
import com.security.corespringsecurity5.security.handler.CustomAuthenticationSuccessHandler;
import com.security.corespringsecurity5.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.security.corespringsecurity5.security.provider.CustomAuthenticationProvider;
import com.security.corespringsecurity5.security.service.SecurityResourceService;
import com.security.corespringsecurity5.security.voter.IpAddressAccessVoter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@EnableWebSecurity
@Order(1)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityResourceService securityResourceService;

    //??????, ????????? ?????? ?????? ?????? Url
    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

    @Autowired
    private AccessDecisionVoter<? extends Object> roleVoter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //?????? ?????? ignore
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //?????? ?????? ??????
        http.authorizeRequests((authorizeRequest) -> {
            /*???????????????, ????????? ????????????, ?????????????????? ?????? ?????? ?????? ??????
            ????????? ????????? /login?error=ture ??? ???????????????, Security??? ????????? ?????? ?????? URL??? ???????????????
            /login* ?????? ??????.
            */
//            authorizeRequest.antMatchers("/", "/users", "/user/login/**", "/login*").permitAll();

//            // /myPage??? USER ????????? ?????? ??????
//            authorizeRequest.antMatchers("/mypage").hasRole("USER");
//            // /messages??? MANAGER??? ?????? ??????
//            authorizeRequest.antMatchers("/messages").hasRole("MANAGER");
//            // /config??? ADMIN??? ?????? ??????
//            authorizeRequest.antMatchers("/config", "/admin/**").hasRole("ADMIN");
            // ??? ????????? url ?????? ?????? ??????
            authorizeRequest.antMatchers("/**").permitAll();

            //??? ??? ?????? ????????? ????????? ???????????? ?????? ??????.
            authorizeRequest.anyRequest().authenticated();
        });

        //?????? ??????
        //????????? ??????
        http.formLogin((formLogin) -> {
            //????????? ????????? URL ??????
            formLogin.loginPage("/login");
           /*
           ????????? ?????? URL ?????? - ?????? ???????????? ????????????????????????
           POST??? ?????? URL??? ????????? Security??? ??????????????? authentication??????????????? ???????????????.
            */
            formLogin.loginProcessingUrl("/login_proc");
            //AuthenticationDetails ??????(secretKey ??????)
            formLogin.authenticationDetailsSource(authenticationDetailsSource());
            //successHandler
            formLogin.successHandler(authenticationSuccessHandler());
            //Failure Handler
            formLogin.failureHandler(authenticationFailureHandler());
        });

        //ExceptionHandle
        http.exceptionHandling((httpSecurityExceptionHandlingConfigurer) -> {
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                //?????? ?????? ????????? redirect
                String deniedUrl = "/denied?exception=" + accessDeniedException.getMessage();
                response.sendRedirect(deniedUrl);
            });
        });
        //?????? FilterSecurityInterceptor Filter??? ?????? CustomFilterSecurityInterceptor??? ?????? ??????????????? add
//        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
    }

    @Bean
    public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
        return new FormAuthenticationDetailsSource();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
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

    //FilterSecurityInterceptor ?????? Filter Bean??????
//    @Bean
//    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
//        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
//        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
//        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
//        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
//
//        return filterSecurityInterceptor;
//    }

    //FilterSecurityInterceptor ???????????? PermitAllFilter ??? ??????
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());

        return permitAllFilter;
    }

    /**
     * ????????? ?????????????????? ???????????? ????????? Filter Enabled false??????
     *
     * @return
     * @throws Exception
     */
    @Bean
    public FilterRegistrationBean<FilterSecurityInterceptor> filterRegistrationBean() throws Exception {
        FilterRegistrationBean<FilterSecurityInterceptor> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(customFilterSecurityInterceptor());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    //AuthenticationManager Bean??????
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private AccessDecisionManager affirmativeBased() {
        //AcceessDecisionManager??? ????????? Voter?????? ????????? ????????????.
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    //Voters
    //AccessDecisionManager??? ??????????????? ????????? Voter List ??????
    //RoleHierarchyVoter List??? ??????
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        //IpAddressVoter??? ?????? ?????? ??????????????? ??????.
        accessDecisionVoters.add(new IpAddressAccessVoter(securityResourceService));
//        accessDecisionVoters.add(roleVoter());
        accessDecisionVoters.add(roleVoter);
        return accessDecisionVoters;
//        return Arrays.asList(new RoleVoter());
    }

//    @Bean
//    public AccessDecisionVoter<? extends Object> roleVoter() {
//        // ????????? ?????? Voter ??????(?????? ?????? ?????????????????? RoleHierarchy ????????????)
//        return new RoleHierarchyVoter(roleHierarchy());
////        return new RoleVoter();
//    }
//
//    @Bean
//    public RoleHierarchyImpl roleHierarchy() {
//        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//        return roleHierarchy;
//    }

    //???????????? URL????????? FilterInvocationSecurityMetadataSource ?????????
    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFacotoryBean().getObject(), securityResourceService);
    }

    public UrlResourcesMapFactoryBean urlResourcesMapFacotoryBean() {
        return new UrlResourcesMapFactoryBean(securityResourceService);
    }

}
