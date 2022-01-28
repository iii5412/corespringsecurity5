package com.security.corespringsecurity5.controller.admin;

import com.security.corespringsecurity5.domain.dto.RoleDto;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    private final ModelMapper modelMapper;

    @GetMapping("/admin/roles")
    public String getRole(Model model){
        model.addAttribute("roles", roleService.getRoles());
        return "/admin/role/list";
    }

    @GetMapping("/admin/roles/register")
    public String viewRoles(Model model) {
        model.addAttribute("role", new RoleDto());
        return "admin/role/detail";
    }

    @PostMapping("/admin/roles")
    public String createRole(RoleDto roleDto) {
        final Role role = modelMapper.map(roleDto, Role.class);
        roleService.createRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}")
    public String getRole(@PathVariable String id, Model model){
        final RoleDto roleDto = new ModelMapper().map(roleService.getRole(Long.parseLong(id)), RoleDto.class);
        model.addAttribute("role", roleDto);
        return "admin/role/detail";
    }

    @GetMapping("/admin/roles/delete/{id}")
    public String removeResources(@PathVariable String id){
        roleService.deleteRole(Long.parseLong(id));
        return "redirect:/admin/roles";
    }
}
