package com.security.corespringsecurity5.controller.admin;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.domain.entity.Role;
import com.security.corespringsecurity5.service.RoleService;
import com.security.corespringsecurity5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserManagerController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/admin/accounts")
    public String getUsers(Model model){
        model.addAttribute("accounts", userService.getUsers());
        return "admin/user/list";
    }

    @PostMapping("/admin/accounts")
    public String modifyUser(AccountDto accountDto) {
        userService.modifyUser(accountDto);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/admin/accounts/{id}")
    public String getUser(@PathVariable Long id, Model model){
        final AccountDto accountDto = userService.getUser(id);
        final List<Role> roleList = roleService.getRoles();

        model.addAttribute("account", accountDto);
        model.addAttribute("roleList", roleList);

        return "admin/user/detail";
    }

    @GetMapping("/admin/accounts/delete/{id}")
    public String removeUser(@PathVariable Long id, Model model) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
