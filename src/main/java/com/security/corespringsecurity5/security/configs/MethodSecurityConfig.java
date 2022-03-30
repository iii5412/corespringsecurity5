package com.security.corespringsecurity5.security.configs;

import com.security.corespringsecurity5.security.factory.MethodResourcesFactoryBean;
import com.security.corespringsecurity5.security.interceptor.CustomMethodSecurityInterceptor;
import com.security.corespringsecurity5.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = false, securedEnabled = true)
@RequiredArgsConstructor
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    private SecurityResourceService securityResourceService;

//    @Autowired
//    private AccessDecisionVoter<? extends Object> roleVoter;

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
//        return super.customMethodSecurityMetadataSource();
        //Map기반으로 설정 할 수 있는 기능을 제공하는 class
        //생성자 파라미터로 메소드 - 권한 매핑 정보를 전달 한다.
        return mapBasedMethodSecurityMetadataSource();
    }

    @Bean
    public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
        return new MapBasedMethodSecurityMetadataSource(methodResourcesFactoryBean().getObject());
    }

    @Bean
    public BeanPostProcessor protectPointcutPostProcessor() {
//        Class<?> clazz = Class.forName("org.springframework.security.config.method.ProtectPointcutPostProcessor");
//        final Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(MapBasedMethodSecurityMetadataSource.class);
//        declaredConstructor.setAccessible(true);
//        final Object instance = declaredConstructor.newInstance(mapBasedMethodSecurityMetadataSource());
//        final Method setPointcutMap = instance.getClass().getMethod("setPointcutMap", Map.class);
//        setPointcutMap.setAccessible(true);
//        setPointcutMap.invoke(instance, pointcutResourcesFactoryBean().getObject());
//
//        return (BeanPostProcessor) instance;
        ProtectPointcutPostProcessor protectPointcutPostProcessor = new ProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource());
        protectPointcutPostProcessor.setPointcutMap(pointcutResourcesFactoryBean().getObject());
        return protectPointcutPostProcessor;
    }

    @Bean
    public MethodResourcesFactoryBean methodResourcesFactoryBean() {
        return new MethodResourcesFactoryBean(securityResourceService, MethodResourcesFactoryBean.METHOD);
    }

    @Bean
    public MethodResourcesFactoryBean pointcutResourcesFactoryBean() {
        return new MethodResourcesFactoryBean(securityResourceService, MethodResourcesFactoryBean.POINT_CUT);
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        // 계층형 권한 Voter 반환(계층 권한 인터페이스인 RoleHierarchy 타입필요)
        return new RoleHierarchyVoter(roleHierarchy());
//        return new RoleVoter();
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new RoleHierarchyImpl();
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = ((AffirmativeBased) super.accessDecisionManager()).getDecisionVoters();
        for (AccessDecisionVoter accessDecisionVoter : decisionVoters) {
            if (accessDecisionVoter instanceof RoleVoter) {
                decisionVoters.remove(accessDecisionVoter);
            }
        }
//        decisionVoters.add(0, roleVoter());
        decisionVoters.add(roleVoter());
        return new AffirmativeBased(decisionVoters);
    }


    @Bean
    public CustomMethodSecurityInterceptor customMethodSecurityInterceptor(MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource){
        final CustomMethodSecurityInterceptor interceptor = new CustomMethodSecurityInterceptor();
        interceptor.setAccessDecisionManager(accessDecisionManager());
        interceptor.setAfterInvocationManager(afterInvocationManager());
        interceptor.setSecurityMetadataSource(mapBasedMethodSecurityMetadataSource);
        final RunAsManager runAsManager = runAsManager();
        if(runAsManager != null){
            interceptor.setRunAsManager(runAsManager);
        }

        return interceptor;
    }
}