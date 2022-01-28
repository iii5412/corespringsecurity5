package com.security.corespringsecurity5.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    @GetMapping("/messages")
    public String messages(){
        return "/user/messages";
    }

    @PostMapping("/api/messages")
    @ResponseBody
    public String getMessages(){
        return "message OK";
    }
}
