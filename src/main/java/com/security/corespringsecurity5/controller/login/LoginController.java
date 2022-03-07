package com.security.corespringsecurity5.controller.login;

import com.security.corespringsecurity5.domain.dto.AccountDto;
import com.security.corespringsecurity5.security.token.AjaxAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final ModelMapper modelMapper;

    @GetMapping(value = {"/login", "/api/login"})
    public String login(@RequestParam(value = "err", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "/user/login/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/login";
    }

    @GetMapping(value = {"/denied", "/api/denied"})
    public String accessDenied(
            @RequestParam(value = "exception", required = false) String exception,
            Principal principal,
            Model model){

        AccountDto accountDto = null;
        if(principal == null){
            return "redirect:/login";
        }
        if(principal instanceof UsernamePasswordAuthenticationToken) {
            accountDto = modelMapper.map(((UsernamePasswordAuthenticationToken) principal).getPrincipal(), AccountDto.class);
        }else if(principal instanceof AjaxAuthenticationToken) {
            accountDto = modelMapper.map(((AjaxAuthenticationToken) principal).getPrincipal(), AccountDto.class);
        }
        model.addAttribute("username", accountDto.getUsername());
        model.addAttribute("exception", exception);

        return "user/login/denied";
    }
}
