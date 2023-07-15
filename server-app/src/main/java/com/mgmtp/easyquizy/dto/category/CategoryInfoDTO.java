package com.mgmtp.easyquizy.dto.category;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryInfoDTO {
    private Long id;

    @NotBlank(message = "Category is required")
    @Size(max = 20, message = "Category must not exceed 20 characters")
    private String name;
}