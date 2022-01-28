package com.security.corespringsecurity5.security.service;

import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("customUserDetailsService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Account account = userRepository.findByUsername(username);
        if(account == null){
            if(userRepository.countByUsername(username) == 0){
                throw new UsernameNotFoundException("사용자 ID를 찾을 수 없습니다");
            }
        }

        final List<SimpleGrantedAuthority> collect = account.getUserRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList())
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new AccountContext(account, collect);
    }
}
