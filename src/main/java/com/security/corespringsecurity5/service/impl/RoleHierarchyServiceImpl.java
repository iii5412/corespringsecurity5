package com.security.corespringsecurity5.service.impl;

import com.security.corespringsecurity5.domain.entity.RoleHierarchy;
import com.security.corespringsecurity5.repository.RoleHierarchyRepository;
import com.security.corespringsecurity5.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    @Override
    public String findAllHierarchy() {
        final List<RoleHierarchy> roleHierarchyList = roleHierarchyRepository.findAll();
        final StringBuilder concatedRoles = new StringBuilder();
        roleHierarchyList.stream().forEach(roleHierarchy -> {
            //parentName(RoleHierarchy type)이 NUll이 아니면
            if(roleHierarchy.getParentRole() != null){
                /*
                    ROLE_ADMIN > ROLE_MANAGER
                    ROLE_MANAGER > ROLE_USER
                 */
                //Parent에서 childName을 가져온다.
                concatedRoles.append(roleHierarchy.getParentRole().getRoleName());
                //규칙에 따라 > 를 append
                concatedRoles.append(" > ");
                concatedRoles.append(roleHierarchy.getRoleName());
                concatedRoles.append("\n");
            }
        });
        return concatedRoles.toString();
    }
}
