package com.security.corespringsecurity5.service.impl;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.ResourcesRepository;
import com.security.corespringsecurity5.repository.RoleRepository;
import com.security.corespringsecurity5.repository.UserRepository;
import com.security.corespringsecurity5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResourcesRepository resourcesRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void createUser(Account account) {
        //처음 사용자 생성시 ROLE_USER 권한을 부여한다.
        //Role을 셋팅해주기위해서 ROLE_USER RoleName으로 Role 조회
        final Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        account.setUserRole(roles);

        userRepository.save(account);
    }

    @Override
    @Transactional
    public void modifyUser(AccountDto accountDto) {

        final Account account = modelMapper.map(accountDto, Account.class);

        if(!accountDto.getRoleNames().isEmpty()){
            Set<Role> roles = new HashSet<>();
            accountDto.getRoleNames().forEach(roleName -> {
                Role r = roleRepository.findByRoleName(roleName);
                roles.add(r);
            });
            account.setUserRole(roles);
        }

        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userRepository.save(account);
    }

    @Override
    public List<Account> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public AccountDto getUser(Long id) throws IllegalArgumentException {
        final Account account = userRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자 ID"));

        final List<String> roleNames = account.getUserRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList());

        final AccountDto accountDto = modelMapper.map(account, AccountDto.class);
        accountDto.setRoleNames(roleNames);
        return accountDto;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
