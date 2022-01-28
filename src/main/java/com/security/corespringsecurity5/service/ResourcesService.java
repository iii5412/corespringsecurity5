package com.security.corespringsecurity5.service;

import com.security.corespringsecurity5.domain.entity.Resources;

import java.util.List;

public interface ResourcesService {

    Resources getResources(Long id);

    List<Resources> getResources();

    Long createResources(Resources resources);

    void deleteResources(Long id);
}
