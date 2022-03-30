package com.security.corespringsecurity5.service.impl;

import com.security.corespringsecurity5.domain.entity.Resources;
import com.security.corespringsecurity5.repository.ResourcesRepository;
import com.security.corespringsecurity5.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResourcedServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;

    @Override
    public Resources getResources(Long id) {
        return resourcesRepository.getById(id);
    }

    @Override
    public List<Resources> getResources() {
        final Sort.Order orderNum = Sort.Order.asc("orderNum");
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    @Override
    @Transactional
    public Long createResources(Resources resources) {
        final Resources savedResources = resourcesRepository.save(resources);
        return savedResources.getId();
    }

    @Override
    @Transactional
    public void deleteResources(Long id) {
        resourcesRepository.deleteById(id);
    }
}
