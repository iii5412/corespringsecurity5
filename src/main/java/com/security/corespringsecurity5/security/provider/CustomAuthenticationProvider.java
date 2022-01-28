package com.security.corespringsecurity5.security.provider;

import com.security.corespringsecurity5.security.common.FormWebAuthenticationDetails;
import com.security.corespringsecurity5.security.service.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.security.corespringsecurity5.security.common.FormWebAuthenticationDetails.SECRET_KEY_NAME;

//@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();

        //DB 사용자 정보 조회
        final AccountContext accountContext = (AccountContext)customUserDetailsService.loadUserByUsername(username);

        //password 검증
        if(!passwordEncoder.matches(password, accountContext.getPassword())){
            throw new BadCredentialsException("비밀번호 불일치");
        }

        //FormWebAuthenticationDetails에서 저장된 파라미터 데이터를 인증에 활용
        final FormWebAuthenticationDetails details = (FormWebAuthenticationDetails) authentication.getDetails();
        if(!SECRET_KEY_NAME.equals(details.getSecretKey())){
            throw new InsufficientAuthenticationException("Secret key 불일치");
        }

        return new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
    }

    //Authetnicaiotn타입과 Provider에서 사용하고자 하는 타입의 일치여부
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
