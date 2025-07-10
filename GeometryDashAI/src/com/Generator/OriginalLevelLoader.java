package com.Generator;

import com.jade.GameObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class OriginalLevelLoader {
    public List<GameObject> loadLevel(String levelName) {
        Map<String, List<String>> originalLevels = PatternLibrary.getOriginalLevels();
        List<String> patternNames = originalLevels.getOrDefault(levelName, new ArrayList<>());

        if (patternNames.isEmpty()) {
            System.err.println("Nie znaleziono definicji dla poziomu: " + levelName);
            return new ArrayList<>();
        }

        List<Pattern> patterns = PatternLibrary.getPatternsFromNames(patternNames);
        LevelChromosome chromosome = new LevelChromosome(patterns);
        GeneratedLevelLoader loader = new GeneratedLevelLoader();
        return loader.translateChromosomeToGameObjects(chromosome);
    }
}