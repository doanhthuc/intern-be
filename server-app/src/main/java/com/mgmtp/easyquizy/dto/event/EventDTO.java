package com.mgmtp.easyquizy.dto.event;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor(force = true)
public class EventDTO {
    private Long id;

    @NotEmpty
    @Size(max = 255, message = "Title must not be exceed 255 characters")
    private String title;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Size(max = 255, message = "Description must not be exceed 255 characters")
    private String description;
}
