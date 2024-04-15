package com.kb.wstictactoe.storage;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class ResetStorage {
    private Map<String, HashSet<String>> resetMap;

    public ResetStorage(){
        resetMap = new HashMap<>();
    }

    public Map<String, HashSet<String>> getResetMap(){
        return resetMap;
    }

    public void putInResetMap(String playerId, String gameId){
        if(resetMap.containsKey(gameId)){
            resetMap.get(gameId).add(playerId);
            return;
        }
        HashSet<String> tempSet = new HashSet<>();
        tempSet.add(playerId);
        resetMap.put(gameId, tempSet);
    }

    public void emptyKeyInResetMap(String gameId){
        resetMap.put(gameId, new HashSet<>());
    }
}
