package com.security.corespringsecurity5.controller.admin;

import com.security.corespringsecurity5.domain.dto.ResourcesDto;
import com.security.corespringsecurity5.domain.entity.Resources;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.repository.RoleRepository;
import com.security.corespringsecurity5.service.ResourcesService;
import com.security.corespringsecurity5.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ResourcesController {

    private final ResourcesService resourcesService;

    private final RoleRepository roleRepository;

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    @GetMapping("/admin/resources")
    public String getResources(Model model) throws Exception {
        model.addAttribute("resources", resourcesService.getResources());
        return "admin/resource/list";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResrouces(@PathVariable String id, Model model) {
        final Resources resources = resourcesService.getResources(Long.parseLong(id));
        model.addAttribute("resources", modelMapper.map(resources, ResourcesDto.class));
        model.addAttribute("roleList", roleService.getRoles());
        return "/admin/resource/detail";
    }

    @PostMapping("/admin/resources/delete/{id}")
    public String removeResrouces(@PathVariable String id, Model model) {
        resourcesService.deleteResources(Long.parseLong(id));
        return "redirect:/admin/resources";
    }

    @PostMapping("/admin/resources")
    public String createResources(ResourcesDto resourcesDto) {
        //자원에 설정한 권한을 찾아온다.
        final Role role = roleRepository.findByRoleName(resourcesDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        resourcesDto.setRoleSet(roles);

        final Resources resources = modelMapper.map(resourcesDto, Resources.class);

        resourcesService.createResources(resources);

        return "redirect:/admin/resources";
    }

    @GetMapping
    public String viewRoles(Model model) {
        model.addAttribute("roleList", roleService.getRoles());

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());

        final ResourcesDto resourcesDto = new ResourcesDto().builder().roleSet(roleSet).build();

        model.addAttribute("resources", resourcesDto);

        return "admin/resource/detail";
    }

}
