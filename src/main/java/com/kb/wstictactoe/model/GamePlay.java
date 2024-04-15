package com.kb.wstictactoe.model;

import lombok.*;

@Data
public class GamePlay {
    private TicTacToe type;
    private Integer coordinateX;
    private Integer coordinateY;
    private String gameId;
}
