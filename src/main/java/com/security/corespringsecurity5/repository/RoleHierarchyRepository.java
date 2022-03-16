package com.security.corespringsecurity5.repository;

import com.security.corespringsecurity5.domain.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
    RoleHierarchy findByRoleName(String roleName);
}
