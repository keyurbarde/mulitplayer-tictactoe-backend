package com.kb.wstictactoe.service;

import com.kb.wstictactoe.exception.InvalidParamException;
import com.kb.wstictactoe.exception.InvalidGameException;
import com.kb.wstictactoe.exception.NotFoundException;
import com.kb.wstictactoe.model.Game;
import com.kb.wstictactoe.model.GamePlay;
import com.kb.wstictactoe.model.GameStatus;
import com.kb.wstictactoe.model.TicTacToe;
import com.kb.wstictactoe.repository.GameRepository;

import com.kb.wstictactoe.storage.ResetStorage;
import com.kb.wstictactoe.storage.SessionStorage;

import org.springframework.stereotype.Service;


@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ResetStorage resetStorage;

    public GameService(GameRepository gameRepository, ResetStorage resetStorage, SessionStorage sessionStorage) {
        this.gameRepository = gameRepository;
        this.resetStorage = resetStorage;
    }

    public Game createGame(String playerId){
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setStatus(GameStatus.NEW);

        String id = Integer.toString((int) (Math.random() * 100000));
        game.setGameId(id);

        game.setPlayer1Id(playerId);
        gameRepository.save(game);

        return game;
    }

    public Game connectToGame(String playerId, String gameId) throws InvalidParamException, InvalidGameException {
        String newGameId = gameId.toUpperCase();
        if (!gameRepository.existsById(newGameId)) {
            throw new InvalidParamException("Game does not exist");
        }

        Game game = gameRepository.findById(newGameId).get();

        if (game.getPlayer2Id() != null) {
            throw new InvalidGameException("Game already has two players");
        }

        game.setPlayer2Id(playerId);
        game.setStatus(GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        return game;
    }


    public Game connectToRandomGame(String playerId) throws NotFoundException {
        boolean gameFound = false;
        Game game = null;
        for(Game it : gameRepository.findAll()){
            if(it.getStatus().equals(GameStatus.NEW)){
                game = it;
                gameFound = true;
                break;
            }
        }
        if(!gameFound){
            throw new NotFoundException("No games present");
        }

        game.setPlayer2Id(playerId);
        game.setStatus(GameStatus.NEW);
        gameRepository.save(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if(!gameRepository.existsById(gamePlay.getGameId())){
            throw new NotFoundException("Game Not Found");
        }

        Game game = gameRepository.findById(gamePlay.getGameId()).get();

        if(game.getPlayer2Id() == null || game.getPlayer1Id() == null){
            throw new InvalidGameException("Game does not have 2 players");
        }

        if(game.getStatus().equals(GameStatus.FINISHED)){
            throw new InvalidGameException("Game is Finished");
        }

        int [][] board = game.getBoard();

        board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();

        if(checkWinner(game.getBoard(), TicTacToe.X)){
            game.setWinner(game.getPlayer1Id());
        }
        else if(checkWinner(game.getBoard(), TicTacToe.O)){
            game.setWinner(game.getPlayer2Id());
        }

        if(game.getWinner() != null){
            game.setStatus(GameStatus.FINISHED);
        }
        else if(isBoardFull(game.getBoard())){
            game.setStatus(GameStatus.FINISHED);
        }

        gameRepository.save(game);
        return game;
    }

    public boolean isBoardFull(int[][] board){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkWinner(int[][] board, TicTacToe playerType) {
        int type = playerType.getValue();

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == type && board[i][1] == type && board[i][2] == type) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < 3; j++) {
            if (board[0][j] == type && board[1][j] == type && board[2][j] == type) {
                return true;
            }
        }

        // Check diagonals
        return (board[0][0] == type && board[1][1] == type && board[2][2] == type) ||
                (board[0][2] == type && board[1][1] == type && board[2][0] == type);
    }

    public boolean resetGame(String playerId, String gameId){

        resetStorage.putInResetMap(playerId, gameId);
        boolean result = false;
        Game game = gameRepository.findById(gameId).get();

        if (resetStorage.getResetMap().get(gameId).size() == 2){
            game.setWinner(null);
            game.setStatus(GameStatus.IN_PROGRESS);
            game.setBoard(new int[3][3]);
            resetStorage.emptyKeyInResetMap(gameId);
            result = true;
        }
            System.out.println(game);
        gameRepository.save(game);

        return result;
    }

}
