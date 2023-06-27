package com.mgmtp.easyquizy.dto;

import lombok.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CategoryDTO {
    private Long id;

    @NotNull(message = "Category is required")
    private String name;
}
