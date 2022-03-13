package com.security.corespringsecurity5.security.metadatasource;

import com.security.corespringsecurity5.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();

    private SecurityResourceService resourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap, SecurityResourceService resourceService) {
        requestMap = resourcesMap;
        this.resourceService = resourceService;
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //filterInvocation에서 사용자의 요청정보를 가져온다.
        final FilterInvocation fi = (FilterInvocation) object;
        final HttpServletRequest request = fi.getRequest();

//        requestMap.put(new AntPathRequestMatcher("/mypage"), Arrays.asList(new SecurityConfig("ROLE_USER")));

        //requestMap이 Null이 아닐때
        if(requestMap != null){
            //reqeustMap에 있는 요청정보와 비교하여 권한 정보를 찾아 반환
            for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()){
                final RequestMatcher requestMatcher = entry.getKey();
                log.info("request : {}", request.getRequestURI());
                if(requestMatcher.matches(request)){
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.requestMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }


    public void reload(){
//        final LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = resourceService.getResourceList();
//
//        final Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();
//
//        requestMap.clear();
//
//        while (iterator.hasNext()){
//            final Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
//            requestMap.put(entry.getKey(), entry.getValue());
//        }

        requestMap.clear();
        requestMap = resourceService.getResourceList();
    }
}
