package com.mgmtp.easyquizy.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}
