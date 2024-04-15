package com.kb.wstictactoe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetRequest {
    private boolean toReset;
    private String playerId;


}
