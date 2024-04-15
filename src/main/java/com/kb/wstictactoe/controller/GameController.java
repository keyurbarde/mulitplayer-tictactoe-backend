    package com.kb.wstictactoe.controller;

    import com.kb.wstictactoe.dto.PlayerGameObj;
    import com.kb.wstictactoe.exception.InvalidParamException;
    import com.kb.wstictactoe.model.Game;
    import com.kb.wstictactoe.service.GameService;
    import com.kb.wstictactoe.dto.RequestPlayerId;
    import com.kb.wstictactoe.dto.ResetRequest;
    import com.kb.wstictactoe.exception.InvalidGameException;
    import com.kb.wstictactoe.exception.NotFoundException;
    import com.kb.wstictactoe.model.GamePlay;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.Payload;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.web.bind.annotation.CrossOrigin;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @Slf4j
    @CrossOrigin(origins = "http://localhost:3000")
    public class GameController {
        private final GameService gameService;
        private final SimpMessagingTemplate simpMessagingTemplate;

        public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
            this.gameService = gameService;
            this.simpMessagingTemplate = simpMessagingTemplate;
        }

        @PostMapping("/start")
        public ResponseEntity<Game> start(@RequestBody RequestPlayerId requestPlayerId){
            return ResponseEntity.ok(gameService.createGame(requestPlayerId.getPlayerId()));
        }

        @PostMapping("/connect")
        public ResponseEntity<Game> connect(@RequestBody PlayerGameObj request) throws InvalidParamException, InvalidGameException {
            log.info("connectRequest: {}", request);
            simpMessagingTemplate.convertAndSend("/topic/player2connected/" + request.getGameId(), request);
            return ResponseEntity.ok(gameService.connectToGame(request.getPlayerId(), request.getGameId()));
        }

        @PostMapping("/connect/random")
        public ResponseEntity<Game> connectRandom(@RequestBody RequestPlayerId playerId) throws NotFoundException {
            return ResponseEntity.ok((gameService.connectToRandomGame(playerId.getPlayerId())));
        }

        @MessageMapping("/reset-request")
        public ResponseEntity<ResetRequest> reset(@RequestBody PlayerGameObj request){
            log.info("reset-request: {}", request);
            ResetRequest response = new ResetRequest(gameService.resetGame(request.getPlayerId(), request.getGameId()), request.getPlayerId());
            simpMessagingTemplate.convertAndSend("/topic/reset-request/" + request.getGameId(), response);
            return ResponseEntity.ok(response);
        }

        @MessageMapping("/gameplay")
        public ResponseEntity<Game> gamePlay(@Payload GamePlay request) throws InvalidGameException, NotFoundException {
            log.info("gameplay: {}", request);
            Game game = gameService.gamePlay(request);
            simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
            return ResponseEntity.ok(game);
        }
    }
