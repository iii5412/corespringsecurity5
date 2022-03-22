package com.security.corespringsecurity5.service;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.domain.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    void createUser(Account account);

    void modifyUser(AccountDto accountDto);

    List<Account> getUsers();

    AccountDto getUser(Long id);

    void deleteUser(Long id);

    void order();
}
