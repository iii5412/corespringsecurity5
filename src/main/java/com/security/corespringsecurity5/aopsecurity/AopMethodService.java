package com.security.corespringsecurity5.aopsecurity;

import org.springframework.stereotype.Service;

@Service
public class AopMethodService {

    public void methodSecured(){
        System.out.println("ApoMethodService.methodSecured");
    }
}
