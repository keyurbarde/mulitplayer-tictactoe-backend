package com.kb.wstictactoe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Game {

    @Id
    private String gameId;
    private String player1Id;
    private String player2Id;
    private GameStatus status;
    private int[][] board;
    private String winner;
}
