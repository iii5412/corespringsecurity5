package com.security.corespringsecurity5.security.init;

import com.security.corespringsecurity5.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;


//부트 가동 시점
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityInitalizer implements ApplicationRunner {

    private final RoleHierarchyService roleHierarchyService;
    private final RoleHierarchyImpl roleHierarchy;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("ApplicationRunner의 run 실행시작!!!");
        /*
            RoleHierarchy 구현체인 RoleHierarchyImpl의 setHierarchy를 통해
            계층 권한 구조를 포매팅한 (ADMIN > MANAGER ..) 문자열을 주입한다.
         */
        roleHierarchy.setHierarchy(roleHierarchyService.findAllHierarchy());
    }
}
