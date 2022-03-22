package com.security.corespringsecurity5.security.configs;

import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@EnableGlobalMethodSecurity(prePostEnabled = false, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
//        return super.customMethodSecurityMetadataSource();
        //Map기반으로 설정 할 수 있는 기능을 제공하는 class
        return new MapBasedMethodSecurityMetadataSource();
    }
}
