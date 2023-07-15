package com.mgmtp.easyquizy.dto.attachment;

import com.mgmtp.easyquizy.model.attachment.AttachmentType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AttachmentDTO {
    private Long id;

    @NotBlank(message = "This field is required")
    private String content;

    @NotNull(message = "This field is required")
    private AttachmentType attachmentType;
}
