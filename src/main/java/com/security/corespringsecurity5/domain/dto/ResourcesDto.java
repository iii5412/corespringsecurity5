package com.security.corespringsecurity5.domain.dto;

import com.security.corespringsecurity5.domain.entity.Role;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourcesDto implements Serializable {
    private Long id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet;
}
