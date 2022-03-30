package com.security.corespringsecurity5.security.factory;

import com.security.corespringsecurity5.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodResourcesFactoryBean implements FactoryBean<LinkedHashMap<String, List<ConfigAttribute>>> {

    private final SecurityResourceService securityResourceService;
    private String resourceType;
    private LinkedHashMap<String, List<ConfigAttribute>> methodMap;

    public static final String METHOD = "method";
    public static final String POINT_CUT = "pointcut";

    public MethodResourcesFactoryBean(SecurityResourceService securityResourceService, String resourceType) {
        this.securityResourceService = securityResourceService;
        this.resourceType = resourceType;
    }

    private void init() {
//        if(METHOD.equals(resourceType)) {
//            methodMap = securityResourceService.getMethodResourceList();
//        }
        switch (resourceType){
            case METHOD:
                methodMap = securityResourceService.getMethodResourceList();
                break;
            case POINT_CUT:
                methodMap = securityResourceService.getPointcutResourceList();
                break;
        }
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
