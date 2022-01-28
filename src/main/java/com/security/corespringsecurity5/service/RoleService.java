package com.security.corespringsecurity5.service;

import com.security.corespringsecurity5.domain.entity.Role;

import java.util.List;

public interface RoleService {

    Role getRole(Long id);

    List<Role> getRoles();

    Long createRole(Role role);

    void deleteRole(Long id);
}
