package com.kb.wstictactoe.storage;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionStorage {
    private final ConcurrentMap<String, String> sessionToPlayerMap;

    public SessionStorage() {
        this.sessionToPlayerMap = new ConcurrentHashMap<>();
    }

    public void associateSessionWithPlayer(String sessionId, String playerId) {
        sessionToPlayerMap.put(sessionId, playerId);
    }

    public String getPlayerIdForSession(String sessionId) {
        return sessionToPlayerMap.get(sessionId);
    }

    public void dissociateSessionFromPlayer(String sessionId) {
        sessionToPlayerMap.remove(sessionId);           
    }
}
