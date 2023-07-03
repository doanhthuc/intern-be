package com.mgmtp.easyquizy.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDTO {
    private Long id;

    @NotBlank(message = "Category is required")
    private String name;
}
