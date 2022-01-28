package com.security.corespringsecurity5.repository;

import com.security.corespringsecurity5.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    int countByUsername(String username);
}
