package com.security.corespringsecurity5.controller.admin;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    @GetMapping("/admin")
    public String home(Model model) throws Exception{
        final List<Account> users = userService.getUsers();
        final List<AccountDto> accountDtos = users.stream()
                .map((account) -> modelMapper.map(account, AccountDto.class)
                ).collect(Collectors.toList());

        model.addAttribute("accounts", accountDtos);

        return "admin/home";
    }
}
