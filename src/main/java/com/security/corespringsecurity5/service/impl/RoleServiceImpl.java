package com.security.corespringsecurity5.service.impl;

import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.RoleRepository;
import com.security.corespringsecurity5.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRole(Long id) {
        return roleRepository.getById(id);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Long createRole(Role role) {
        final Role savedRole = roleRepository.save(role);
        return savedRole.getId();
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
