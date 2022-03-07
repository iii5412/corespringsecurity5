package com.security.corespringsecurity5.security.factory;

import com.security.corespringsecurity5.security.service.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DB에 있는 자원별 권한정보를 Map객체에 매핑하여 LinkedHashMap을 Bean으로 등록하는 BeanFactory
 */
public class UrlResourcesMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    //DB로 부터 가져온 데이터를 매핑 작업을 하는 Service
    private final SecurityResourceService securityResourceService;
    //Bean으로 등록될 Map 객체
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap;

    public UrlResourcesMapFactoryBean(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    /**
     * FactoryBean은 getObject 메소드를 통해 Bean이 된다.
     * resourceMap 생성
     * @return
     * @throws Exception
     */
    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {
        if(resourceMap == null){
            init();
        }
        return resourceMap;
    }

    private void init() {
        resourceMap = securityResourceService.getResourceList();
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
