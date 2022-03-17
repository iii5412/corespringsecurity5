package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.factory.UrlResourcesMapFactoryBean;
import com.security.corespringsecurity5.security.filter.PermitAllFilter;
import com.security.corespringsecurity5.security.handler.CustomAuthenticationFailureHandler;
import com.security.corespringsecurity5.security.handler.CustomAuthenticationSuccessHandler;
import com.security.corespringsecurity5.security.common.FormAuthenticationDetailsSource;
import com.security.corespringsecurity5.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.security.corespringsecurity5.security.provider.CustomAuthenticationProvider;
import com.security.corespringsecurity5.security.service.SecurityResourceService;
import com.security.corespringsecurity5.security.voter.IpAddressAccessVoter;
import com.security.corespringsecurity5.service.RoleHierarchyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Order(1)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityResourceService securityResourceService;

    //인증, 인가가 필요 없는 자원 Url
    private String[] permitAllResources = {"/", "/login", "/user/login/**"};

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
//            authorizeRequest.antMatchers("/", "/users", "/user/login/**", "/login*").permitAll();

//            // /myPage는 USER 권한만 접근 가능
//            authorizeRequest.antMatchers("/mypage").hasRole("USER");
//            // /messages는 MANAGER만 접근 가능
//            authorizeRequest.antMatchers("/messages").hasRole("MANAGER");
//            // /config는 ADMIN만 접근 가능
//            authorizeRequest.antMatchers("/config", "/admin/**").hasRole("ADMIN");
            // 위 지정된 url 외에 모두 허용
            authorizeRequest.antMatchers("/**").permitAll();

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
        //기존 FilterSecurityInterceptor Filter의 앞에 CustomFilterSecurityInterceptor를 먼저 수행하도록 add
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

    //FilterSecurityInterceptor 타입 Filter Bean등록
//    @Bean
//    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {
//        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
//        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
//        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
//        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
//
//        return filterSecurityInterceptor;
//    }

    //FilterSecurityInterceptor 상속받은 PermitAllFilter 로 변경
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());

        return permitAllFilter;
    }

    /**
     * 서블릿 컨테이너에서 사용되지 않도록 Filter Enabled false처리
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

    //AuthenticationManager Bean등록
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private AccessDecisionManager affirmativeBased() {
        //AcceessDecisionManager는 생성시 Voter들이 필수로 필요하다.
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    //Voters
    //AccessDecisionManager가 인가처리시 참고할 Voter List 반환
    //RoleHierarchyVoter List를 반환
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        //IpAddressVoter가 제일 먼저 수행되어야 한다.
        accessDecisionVoters.add(new IpAddressAccessVoter(securityResourceService));
        accessDecisionVoters.add(roleVoter());
        return accessDecisionVoters;
//        return Arrays.asList(new RoleVoter());
    }

//    @Bean
//    public AccessDecisionVoter<? extends Object> roleVoter() {
    private AccessDecisionVoter<? extends Object> roleVoter() {
        // 계층형 권한 Voter 반환(계층 권한 인터페이스인 RoleHierarchy 타입필요)
        return new RoleHierarchyVoter(roleHierarchy());
//        return new RoleVoter();
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }

    //직접만든 URL방식의 FilterInvocationSecurityMetadataSource 구현체
    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourcesMapFacotoryBean().getObject(), securityResourceService);
    }

    public UrlResourcesMapFactoryBean urlResourcesMapFacotoryBean() {
        return new UrlResourcesMapFactoryBean(securityResourceService);
    }

}
