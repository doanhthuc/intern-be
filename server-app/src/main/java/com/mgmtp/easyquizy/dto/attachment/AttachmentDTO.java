package com.mgmtp.easyquizy.dto.attachment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AttachmentDTO {
    private Long id;

    @NotBlank(message = "This field is required")
    private String imageData;

    private String sourceCode;

    private String kahootUrl;

    private Boolean isUploaded;
}
