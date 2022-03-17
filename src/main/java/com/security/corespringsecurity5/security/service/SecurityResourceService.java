package com.security.corespringsecurity5.security.service;

import com.security.corespringsecurity5.domain.entity.AccessIp;
import com.security.corespringsecurity5.domain.entity.Resources;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.AccessIpRepository;
import com.security.corespringsecurity5.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 데이터 계층으로부터 자원 정보, 권한정보를 가져와 Map객체로 매핑한다.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SecurityResourceService {

    final private ResourcesRepository resourcesRepository;
    final private AccessIpRepository accessIpRepository;

    /*
    public void setResourcesRepository(ResourcesRepository resourcesRepository) {
        this.resourcesRepository = resourcesRepository;
    }
    */

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        final List<Resources> resourcesList = resourcesRepository.findAllResources();
        resourcesList.forEach(resource -> {
            List<ConfigAttribute> configAttributeList = new ArrayList<>();
            resource.getRoleSet().forEach(role -> {
                configAttributeList.add(new SecurityConfig(role.getRoleName()));
            });
            result.put(new AntPathRequestMatcher(resource.getResourceName()), configAttributeList);
        });
        return result;
    }

    public List<String> getAccessIpList() {
//        return accessIpRepository.findAll().stream()
//                .map(accessIp -> accessIp.getIpAddress()).collect(Collectors.toList());
        return accessIpRepository.findAll().stream()
                .map(AccessIp::getIpAddress).collect(Collectors.toList());
    }
}
