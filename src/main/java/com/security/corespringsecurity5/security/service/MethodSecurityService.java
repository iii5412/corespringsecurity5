package com.security.corespringsecurity5.security.service;


import com.security.corespringsecurity5.security.interceptor.CustomMethodSecurityInterceptor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MethodSecurityService {
    private final MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource;
    private final AnnotationConfigServletWebServerApplicationContext applicationContext;
    private final CustomMethodSecurityInterceptor methodSecurityInterceptor;

    private Map<String, Object> proxyMap = new HashMap<>();
    private Map<String, ProxyFactory> advisedMap = new HashMap<>();
    private Map<String, Object> targetMap = new HashMap<>();

    public void addMethodSecured(String className, String roleName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        final ClassInfo classInfo = new ClassInfo(className);
        final String methodName = classInfo.getMethodName();
        final Class<?> type = classInfo.getType();
        final String beanName = classInfo.getBeanName();

        ProxyFactory proxyFactory = advisedMap.get(beanName);
        final Object target = targetMap.get(beanName);

        if(proxyFactory == null){

            proxyFactory = new ProxyFactory();
            if(target == null){
                proxyFactory.setTarget(type.getDeclaredConstructor().newInstance());
            }else{
                proxyFactory.setTarget(target);
            }
            proxyFactory.addAdvice(methodSecurityInterceptor);
            advisedMap.put(beanName, proxyFactory);
        } else {
            final int adviceIndex = proxyFactory.indexOf(methodSecurityInterceptor);
            if(adviceIndex == -1){
                proxyFactory.addAdvice(methodSecurityInterceptor);
            }
        }

        Object proxy = proxyMap.get(beanName);

        if(proxy == null){
            proxy = proxyFactory.getProxy();
            proxyMap.put(beanName, proxy);
            List<ConfigAttribute> attr = Collections.singletonList(new SecurityConfig(roleName));
            mapBasedMethodSecurityMetadataSource.addSecureMethod(type, methodName, attr);
            final DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getBeanFactory();
            registry.destroySingleton(beanName);
            registry.registerSingleton(beanName, proxy);
        }else {
            //이미 있는경우 권한만 바꿔치기 하려는데..methodMap에 추가는 가능하지만 삭제가 .. 안되는것 같다..ㅜ
            final Class<?> clazz = Class.forName(type.getName());
            final Method method = clazz.getMethod(methodName);
            final Collection<ConfigAttribute> attributes = mapBasedMethodSecurityMetadataSource.getAttributes(method, clazz);
            final String sameAttr = attributes.stream().filter(configAttribute -> configAttribute.getAttribute().equals(roleName)).findFirst().map(ConfigAttribute::getAttribute).orElse("");

            if(sameAttr.equals("")){
                //remove나 modify 할 수 있는 메소드가 없다..;;
//                mapBasedMethodSecurityMetadataSource.
            }


        }
    }

    public void removeMethodSecured(String className) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final ClassInfo classInfo = new ClassInfo(className);
        final String methodName = classInfo.getMethodName();
        final Class<?> type = classInfo.getType();
        final String beanName = classInfo.getBeanName();
        final Object newInstance = type.getDeclaredConstructor().newInstance();

        final DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getBeanFactory();

        final ProxyFactory proxyFactory = advisedMap.get(beanName);

        if(proxyFactory != null){
            proxyFactory.removeAdvice(methodSecurityInterceptor);
            proxyMap.remove(beanName);
        }else{
            registry.destroySingleton(beanName);
            registry.registerSingleton(beanName, newInstance);
            targetMap.put(beanName, newInstance);
        }
    }

    @Getter
    static class ClassInfo {
        private String className;
        private int lastDotIndex;
        private String methodName;
        private String typeName;
        private Class<?> type;
        private String beanName;

        public ClassInfo(String className) {
            this.className = className;
            this.lastDotIndex = className.lastIndexOf(".");
            this.methodName = className.substring(this.lastDotIndex + 1);
            this.typeName = className.substring(0, this.lastDotIndex);
            this.type = ClassUtils.resolveClassName(typeName, ClassUtils.getDefaultClassLoader());
            this.beanName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
        }
    }
}
