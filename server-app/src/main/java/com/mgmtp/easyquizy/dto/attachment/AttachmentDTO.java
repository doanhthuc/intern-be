package com.mgmtp.easyquizy.dto.attachment;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AttachmentDTO {
    private Long id;

    @NotBlank(message = "This field is required")
    @Size(max = 1024 * 1024 * 4, message = "Image size must not exceed 4MB")
    private String imageData;

    private String sourceCode;

    private String languageType;

    private String kahootUrl;
}
