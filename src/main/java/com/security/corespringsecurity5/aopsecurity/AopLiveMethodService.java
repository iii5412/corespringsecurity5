package com.security.corespringsecurity5.aopsecurity;

import org.springframework.stereotype.Service;

@Service
public class AopLiveMethodService {
    public void liveMethodSecured(){
        System.out.println("AopLiveMethodService.liveMethodSecured");
    }
}
