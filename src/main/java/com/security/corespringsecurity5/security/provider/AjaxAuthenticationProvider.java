package com.security.corespringsecurity5.security.provider;

import com.security.corespringsecurity5.security.common.FormWebAuthenticationDetails;
import com.security.corespringsecurity5.security.service.AccountContext;
import com.security.corespringsecurity5.security.token.AjaxAuthenticationToken;
import com.security.corespringsecurity5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

/*
Ajax 인증 처리를 담당하는 AuthenticationManager를 구현한 ProviderManager에서
    인증을 위임받는 클래스인  AuthenticationProvider 구현체
    https://iii5412.github.io/SpringSecurity/filter_8/
    
 */

@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();

        //DB에서 사용자 정보 조회
        //반환 타입이 UserDetails지만, UserDetails를 구현한 USER클래스를 상속받은 AccountContext로 Casting
        final AccountContext accountContext = (AccountContext)customUserDetailsService.loadUserByUsername(username);

        //password 검증
        if(!passwordEncoder.matches(password, accountContext.getPassword())){
            throw new BadCredentialsException("비밀번호 불일치");
        }

        //로그인시 Authentication Details에 담은 secret Key 검증
        final FormWebAuthenticationDetails details = (FormWebAuthenticationDetails)authentication.getDetails();
        if(details == null || !SECRET_KEY_NAME.equals(details.getSecretKey())){
            //불충분 인증 예외
            throw new InsufficientAuthenticationException("Secret Key 불일치");
        }
        
        //Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
    }

    //Authentication 타입과 Provider에서 인증 객체 검색 시 지원 여부 체킹
    @Override
    public boolean supports(Class<?> authentication) {
        return AjaxAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
