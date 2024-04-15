package com.kb.wstictactoe.event;

import com.kb.wstictactoe.dto.GameP1P2;
import com.kb.wstictactoe.repository.GameRepository;
import com.kb.wstictactoe.storage.SessionStorage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompEventListener {

    private final SessionStorage sessionStorage;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameRepository gameRepository;


    public StompEventListener(SessionStorage sessionStorage, SimpMessagingTemplate messagingTemplate, GameRepository gameRepository) {
        this.sessionStorage = sessionStorage;
        this.messagingTemplate = messagingTemplate;
        this.gameRepository = gameRepository;
    }

    @EventListener
    public void handleStompConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String playerId = headerAccessor.getNativeHeader("playerId").stream().findFirst().get();
        String sessionId = headerAccessor.getSessionId();
        sessionStorage.associateSessionWithPlayer(sessionId, playerId);
        System.out.println("User connected. SessionId: " + sessionId + ", UserId: " + playerId);
    }

    @EventListener
    public void handleStompDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();
        String playerId = sessionStorage.getPlayerIdForSession(sessionId);
        GameP1P2 response = gameRepository.findByPlayerId(playerId);

        System.out.println(response.toString());

        if(response.getPlayer1Id() == null || response.getPlayer2Id() == null) {
            gameRepository.deleteById(response.getGameId());
        }
        else{
            gameRepository.resetPlayerId(playerId);
        }

        messagingTemplate.convertAndSend("/topic/disconnect-check/" + response.getGameId(), playerId);
        sessionStorage.dissociateSessionFromPlayer(sessionId);
        System.out.println("Disconnected from game: " + response.getGameId());
        System.out.println(playerId + " disconnected. SessionId: " + sessionId);
    }
}
