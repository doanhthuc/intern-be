package com.mgmtp.easyquizy.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDTO {
    @NotNull(message = "This field is required")
    private Long id;
    private String name;
}
