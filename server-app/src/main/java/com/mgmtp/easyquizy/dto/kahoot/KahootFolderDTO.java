package com.mgmtp.easyquizy.dto.kahoot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class KahootFolderDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @JsonProperty("folders")
    private List<KahootFolderDTO> folders;
}