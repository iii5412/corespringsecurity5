package com.security.corespringsecurity5.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.security.token.AjaxAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        //Filter 작동 조건 URL 설정
        super(new AntPathRequestMatcher("/api/login"));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        //Ajax 요청인지 체크
        if(!isAjax(request)){
            throw new IllegalStateException("Authenticationis not supported");
        }

        //사용자가 입력한 username과 password를 가져와서 인증을 요청한다.
        //json 포맷으로 데이터가 넘어오기 때문에 ObjectMapper를 이용하여 UnSerialize(역직렬화) 진행.
        final AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
        //username password의 존재 유무 체크
        if(StringUtils.isEmpty(accountDto.getUsername()) || StringUtils.isEmpty(accountDto.getPassword())){
            throw new IllegalArgumentException("Username or Password is empty.");
        }

        //인증토큰 생성(인증 전 생성자)
        final AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());

        //AuthenticationManager에게 인증 처리 요청
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);

    }

    /**
     * Ajax인지 체크하는 방법은 client와 특정 문자열을 header에 담아서 보내기로 약속했다고 하자.
     * header =  X-Requested-With : XMLHttpRequest
     * @param request
     * @return
     */
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Reqeusted-With"));
    }
}
