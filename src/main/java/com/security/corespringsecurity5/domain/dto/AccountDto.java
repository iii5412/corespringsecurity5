package com.security.corespringsecurity5.domain.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements Serializable {
    private Long id;
    private String username;
    private String email;
    private int age;
    private String password;
    private List<String> roleNames;
}
