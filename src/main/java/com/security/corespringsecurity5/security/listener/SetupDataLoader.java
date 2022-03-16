package com.security.corespringsecurity5.security.listener;

import com.security.corespringsecurity5.controller.admin.ResourcesController;
import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.domain.entity.Resources;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.domain.entity.RoleHierarchy;
import com.security.corespringsecurity5.repository.ResourcesRepository;
import com.security.corespringsecurity5.repository.RoleHierarchyRepository;
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

    private final RoleHierarchyRepository roleHierarchyRepository;

    private final PasswordEncoder passwordEncoder;

    private static AtomicInteger count = new AtomicInteger(0);


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        setupSecurityResources();
    }

    private void setupSecurityResources() {
        Set<Role> roles = new HashSet<>();
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", "관리자");
        roles.add(adminRole);
        createResourceIfNotFound("/admin/**", "", roles, "url");
        createUserIfNotFound("admin", "1234", "admin@gmail.com", 10, roles);

        Set<Role> roles1 = new HashSet<>();
        Role managerRole = createRoleIfNotFound("ROLE_MANAGER", "매니저");
        roles1.add(managerRole);
        createUserIfNotFound("manager", "1234", "manager@gmail.com", 10, roles1);
        createRoleHierarchyIfNotFound(managerRole, adminRole);

        Set<Role> roles3 = new HashSet<>();
        Role userRole = createRoleIfNotFound("ROLE_USER", "사용자");
        createResourceIfNotFound("/user/**", "", roles3, "url");
        createUserIfNotFound("user", "1234", "user@gamil.com", 20, roles3);
        createRoleHierarchyIfNotFound(userRole, managerRole);

    }

    @Transactional
    public Account createUserIfNotFound(String username, String password, String email, int age, Set<Role> roles) {
        Account account = userRepository.findByUsername(username);
        if (account == null) {
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
        if (resources == null) {
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

        if (role == null) {
            role = Role.builder()
                    .roleName(roleName)
                    .roleDesc(roleDesc)
                    .build();
        }

        return roleRepository.save(role);
    }

    //Hierarchy 권한 데이터 적재
    @Transactional
    public void createRoleHierarchyIfNotFound(Role role, Role parentRole) {
        RoleHierarchy roleHierarchy = roleHierarchyRepository.findByRoleName(parentRole.getRoleName());

        if (roleHierarchy == null) {
            roleHierarchy = RoleHierarchy.builder()
                    .roleName(parentRole.getRoleName())
                    .build();
        }

        final RoleHierarchy parentRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);

        roleHierarchy = roleHierarchyRepository.findByRoleName(role.getRoleName());
        if(roleHierarchy == null){
            roleHierarchy = RoleHierarchy.builder()
                    .roleName(role.getRoleName())
                    .build();
        }

        final RoleHierarchy saveRoleHierarchy = roleHierarchyRepository.save(roleHierarchy);
        saveRoleHierarchy.setParentRole(parentRoleHierarchy);
    }
}
