package com.mgmtp.easyquizy.dto.kahoot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KahootCreateDraftRequestDTO {
    private KahootQuizDTO kahoot;
}
