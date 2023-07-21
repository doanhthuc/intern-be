package com.mgmtp.easyquizy.dto.kahoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageToKahootResultDto {
    private String id;
    private String uri;
    private String contentType;
    private String contentLength;
    private String width;
    private String height;
    private String filename;
    private String modificationAllowed;
    private String caption;
}