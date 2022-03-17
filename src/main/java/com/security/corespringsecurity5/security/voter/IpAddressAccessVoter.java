package com.security.corespringsecurity5.security.voter;

import com.security.corespringsecurity5.security.service.SecurityResourceService;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;

public class IpAddressAccessVoter implements AccessDecisionVoter<Object> {
    private SecurityResourceService securityResourceService;
    public IpAddressAccessVoter(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    /**
     * authentication : 인증정보
     * object : 요청정보(FilterInvocation)
     * collection : 자원접근에 필요한 권한정보
     */
    public int vote(Authentication authentication, Object object, Collection collection) {
        System.out.println("IpAddressAccessVoter.vote");
        System.out.println("authentication = " + authentication + ", object = " + object + ", collection = " + collection);

        final WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
        final String remoteAddress = details.getRemoteAddress();
        final List<String> accessIpList = securityResourceService.getAccessIpList();

        int result = ACCESS_DENIED;
        //DB에 설정된 IP면 허용, 아니면 예외 발생
        if(accessIpList.stream().anyMatch(accessIp -> accessIp.equals(remoteAddress))){
            result = ACCESS_ABSTAIN;
        }

        if(result == ACCESS_DENIED){
            throw new AccessDeniedException("Invalid Ip Address.");
        }

        return result;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }
}
