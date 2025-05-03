package com.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DifficultyManager {
    private static DifficultyManager instance = null;
    private Map<String, Difficulty> levelDifficulties;

    private DifficultyManager() {
        levelDifficulties = new HashMap<>();
        levelDifficulties.put("Level 1", Difficulty.EASY);
        levelDifficulties.put("Level 2", Difficulty.HARD);

    }

    public static DifficultyManager getInstance() {
        if (instance == null) {
            instance = new DifficultyManager();
        }
        return instance;
    }
    public Difficulty getDifficultyForLevel(String levelId) {
        return levelDifficulties.getOrDefault(levelId, Difficulty.EASY);
    }

    public void handleLoss() {
        List<String> keys = new ArrayList<>(levelDifficulties.keySet());
        for (String levelId : keys) {
            Difficulty current = levelDifficulties.get(levelId);
            Difficulty next = (current == Difficulty.EASY) ? Difficulty.HARD : Difficulty.EASY;
            levelDifficulties.put(levelId, next);
            System.out.println("  " + levelId + ": " + current + " -> " + next);
        }
        System.out.println("Current difficulties: " + levelDifficulties);
    }

    public void resetDifficulties() {
        levelDifficulties.put("Level 1", Difficulty.EASY);
        levelDifficulties.put("Level 2", Difficulty.HARD);
    }

    public List<String> getAvailableLevels() {
        return new ArrayList<>(levelDifficulties.keySet());
    }
}
