package com.security.corespringsecurity5.controller.user;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.domain.entity.Account;
import com.security.corespringsecurity5.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

//    @GetMapping("/mypage")
//    public String myPage(){
//        return "user/mypage";
//    }

    @GetMapping("/users")
    public String createUser(){
        return "user/login/register";
    }

    @PostMapping("/users")
    public String createUser(AccountDto accountDto){
        final Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userService.createUser(account);

        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPage(@AuthenticationPrincipal Account account, Authentication authentication, Principal principal) {
        //@Secured 테스트로 추가함.
        userService.order();

        return "user/mypage";
    }
}
