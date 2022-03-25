package com.security.corespringsecurity5.security.factory;

import com.security.corespringsecurity5.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodResourcesFactoryBean implements FactoryBean<LinkedHashMap<String, List<ConfigAttribute>>> {

    private final SecurityResourceService securityResourceService;
    private LinkedHashMap<String, List<ConfigAttribute>> methodMap;

    public MethodResourcesFactoryBean(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    private void init() {
        methodMap = securityResourceService.getMethodResourceList();
    }

    @Override
    public LinkedHashMap<String, List<ConfigAttribute>> getObject() {

        if (methodMap == null) {
            init();
        }

        return methodMap;
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
