package com.kb.wstictactoe.repository;

import com.kb.wstictactoe.dto.GameP1P2;
import com.kb.wstictactoe.model.Game;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GameRepository extends CrudRepository<Game, String> {

    @Query("SELECT new com.kb.wstictactoe.dto.GameP1P2(g.gameId, g.player1Id, g.player2Id) FROM Game g WHERE g.player1Id = :playerId OR g.player2Id = :playerId")
    GameP1P2 findByPlayerId(String playerId);

    @Transactional
    @Modifying
    @Query("UPDATE Game SET player1Id = CASE WHEN player1Id = ?1 THEN NULL ELSE player1Id END, player2Id = CASE WHEN player2Id = ?1 THEN NULL ELSE player2Id END WHERE player1Id = ?1 OR player2Id = ?1")
    void resetPlayerId(String playerId);

}