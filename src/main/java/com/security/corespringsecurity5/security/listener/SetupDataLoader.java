package com.security.corespringsecurity5.security.listener;

import com.security.corespringsecurity5.controller.admin.ResourcesController;
import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.domain.entity.Resources;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.ResourcesRepository;
import com.security.corespringsecurity5.repository.RoleRepository;
import com.security.corespringsecurity5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ResourcesRepository resourcesRepository;

    private final PasswordEncoder passwordEncoder;

    private static AtomicInteger count = new AtomicInteger(0);



    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(alreadySetup){
            return;
        }

        setupSecurityResources();
    }

    private void setupSecurityResources() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        createResourceIfNotFound("/admin/**", "", roles, "url");
        createUserIfNotFound("admin","1234","admin@gmail.com", 10, roles);
    }

    @Transactional
    public Account createUserIfNotFound(String username, String password, String email, int age, Set<Role> roles) {
        Account account = userRepository.findByUsername(username);
        if (account == null){
            account = Account.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .age(age)
                    .userRoles(roles)
                    .build();
        }

        return userRepository.save(account);
    }

    @Transactional
    public Resources createResourceIfNotFound(String resourceName, String httpMethod, Set<Role> roles, String resourceType) {
        Resources resources = resourcesRepository.findByResourceNameAndHttpMethod(resourceName, httpMethod);
        if(resources == null) {
            resources = Resources.builder()
                    .resourceName(resourceName)
                    .roleSet(roles)
                    .httpMethod(httpMethod)
                    .resourceType(resourceType)
                    .orderNum(count.decrementAndGet())
                    .build();
        }

        return resourcesRepository.save(resources);
    }

    @Transactional
    public Role createRoleIfNotFound(String roleName, String roleDesc) {
        Role role = roleRepository.findByRoleName(roleName);

        if(role == null){
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }

        return roleRepository.save(role);
    }
}
