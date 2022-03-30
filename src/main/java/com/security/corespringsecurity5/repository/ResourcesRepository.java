package com.security.corespringsecurity5.repository;

import com.security.corespringsecurity5.domain.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourcesRepository extends JpaRepository<Resources, Long> {
    Resources findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = :resourceType order by r.orderNum desc")
    List<Resources> findAllResources(@Param("resourceType") String resourceType);

//    @Query("select r from Resources r join fetch r.roleSet where r.resourceType = 'method' order by r.orderNum desc")
//    List<Resources> findAllMethodResources();

}
