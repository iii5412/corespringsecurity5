package com.security.corespringsecurity5.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@ToString(exclude = {"users", "resourcesSet"})
@NoArgsConstructor
//@EqualsAndHashCode. = 모든 데이터를 비교말고 id만 같아도 같다고 하자. 아니면 연관관계를 모두 조회하여 비교하는 수가 있다..
@EqualsAndHashCode(of = "id")
public class Role {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles")
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roleSet")
    @OrderBy("orderNum desc")
    private Set<Resources> resourcesSet = new LinkedHashSet<>();

}