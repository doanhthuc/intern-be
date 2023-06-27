package com.mgmtp.easyquizy.dto;

import com.mgmtp.easyquizy.model.attachment.AttachmentType;
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
    private String content;

    private AttachmentType attachmentType;
}
