package com.security.corespringsecurity5.security.configs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public class ProtectPointcutPostProcessor implements BeanPostProcessor {
    private static final Log logger = LogFactory.getLog(com.security.corespringsecurity5.security.configs.ProtectPointcutPostProcessor.class);

    private final Map<String, List<ConfigAttribute>> pointcutMap = new LinkedHashMap<>();

    private final MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource;

    private final Set<PointcutExpression> pointCutExpressions = new LinkedHashSet<>();

    private final PointcutParser parser;

    private final Set<String> processedBeans = new HashSet<>();

    ProtectPointcutPostProcessor(MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource) {
        Assert.notNull(mapBasedMethodSecurityMetadataSource,
                "MapBasedMethodSecurityMetadataSource to populate is required");
        this.mapBasedMethodSecurityMetadataSource = mapBasedMethodSecurityMetadataSource;
        // Set up AspectJ pointcut expression parser
        Set<PointcutPrimitive> supportedPrimitives = new HashSet<>(3);
        supportedPrimitives.add(PointcutPrimitive.EXECUTION);
        supportedPrimitives.add(PointcutPrimitive.ARGS);
        supportedPrimitives.add(PointcutPrimitive.REFERENCE);
        this.parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
                        supportedPrimitives);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.processedBeans.contains(beanName)) {
            // We already have the metadata for this bean
            return bean;
        }
        synchronized (this.processedBeans) {
            // check again synchronized this time
            if (this.processedBeans.contains(beanName)) {
                return bean;
            }
            // Obtain methods for the present bean
            Method[] methods = getBeanMethods(bean);
            // Check to see if any of those methods are compatible with our pointcut
            // expressions
            for (Method method : methods) {
                for (PointcutExpression expression : this.pointCutExpressions) {
                    // Try for the bean class directly
                    if (attemptMatch(bean.getClass(), method, expression, beanName)) {
                        // We've found the first expression that matches this method, so
                        // move onto the next method now
                        break; // the "while" loop, not the "for" loop
                    }
                }
            }
            this.processedBeans.add(beanName);
        }
        return bean;
    }

    private Method[] getBeanMethods(Object bean) {
        try {
            return bean.getClass().getMethods();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    private boolean attemptMatch(Class<?> targetClass, Method method, PointcutExpression expression, String beanName) {
        boolean matches = false;
        try {
            // Determine if the presented AspectJ pointcut expression matches this method
            matches = expression.matchesMethodExecution(method).alwaysMatches();
            // Handle accordingly
            if (matches) {
                List<ConfigAttribute> attr = this.pointcutMap.get(expression.getPointcutExpression());
                if (logger.isDebugEnabled()) {
                    logger.debug("AspectJ pointcut expression '" + expression.getPointcutExpression()
                            + "' matches target class '" + targetClass.getName() + "' (bean ID '" + beanName
                            + "') for method '" + method + "'; registering security configuration attribute '" + attr
                            + "'");
                }
                this.mapBasedMethodSecurityMetadataSource.addSecureMethod(targetClass, method, attr);
            }
        }catch (ReflectionWorld.ReflectionWorldException e){
            matches = false;
        }
        return matches;
    }

    public void setPointcutMap(Map<String, List<ConfigAttribute>> map) {
        Assert.notEmpty(map, "configAttributes cannot be empty");
        for (String expression : map.keySet()) {
            List<ConfigAttribute> value = map.get(expression);
            addPointcut(expression, value);
        }
    }

    private void addPointcut(String pointcutExpression, List<ConfigAttribute> definition) {
        Assert.hasText(pointcutExpression, "An AspectJ pointcut expression is required");
        Assert.notNull(definition, "A List of ConfigAttributes is required");
        pointcutExpression = replaceBooleanOperators(pointcutExpression);
        this.pointcutMap.put(pointcutExpression, definition);
        // Parse the presented AspectJ pointcut expression and add it to the cache
        this.pointCutExpressions.add(this.parser.parsePointcutExpression(pointcutExpression));
        if (logger.isDebugEnabled()) {
            logger.debug("AspectJ pointcut expression '" + pointcutExpression
                    + "' registered for security configuration attribute '" + definition + "'");
        }
    }

    private String replaceBooleanOperators(String pcExpr) {
        pcExpr = StringUtils.replace(pcExpr, " and ", " && ");
        pcExpr = StringUtils.replace(pcExpr, " or ", " || ");
        pcExpr = StringUtils.replace(pcExpr, " not ", " ! ");
        return pcExpr;
    }
}
