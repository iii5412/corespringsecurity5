package com.security.corespringsecurity5.security.filter;

import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermitAllFilter extends FilterSecurityInterceptor {
    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";
    private boolean observeOncePerRequest = true;
    private List<RequestMatcher> permitAllRequestMatchers = new ArrayList<>();

    public PermitAllFilter(String... permitAllReources) {
        Arrays.stream(permitAllReources).forEach(resource -> permitAllRequestMatchers.add(new AntPathRequestMatcher(resource)));
    }

    @Override
    protected InterceptorStatusToken beforeInvocation(Object object) {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        boolean permitAll = permitAllRequestMatchers.stream().anyMatch(reqeustMatcher -> reqeustMatcher.matches(request));

        if (permitAll) {
            return null;
        }

        return super.beforeInvocation(object);

//        boolean permitAll = false;
//        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
//        for(RequestMatcher requestMatcher : permitAllRequestMatchers){
//            if(requestMatcher.matches(request)){
//                permitAll = true;
//                break;
//            }
//        }
//
//        if(permitAll){
//            return null;
//        }
//
//        return super.beforeInvocation(object);
    }

    public void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        if (this.isApplied(filterInvocation) && this.observeOncePerRequest) {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        } else {
            if (filterInvocation.getRequest() != null && this.observeOncePerRequest) {
                filterInvocation.getRequest().setAttribute("__spring_security_filterSecurityInterceptor_filterApplied", Boolean.TRUE);
            }

//            InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
            InterceptorStatusToken token = this.beforeInvocation(filterInvocation);

            try {
                filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            } finally {
                super.finallyInvocation(token);
            }

            super.afterInvocation(token, (Object) null);
        }
    }


    private boolean isApplied(FilterInvocation filterInvocation) {
        return filterInvocation.getRequest() != null && filterInvocation.getRequest().getAttribute("__spring_security_filterSecurityInterceptor_filterApplied") != null;
    }
}
