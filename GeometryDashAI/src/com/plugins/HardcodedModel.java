package com.plugins;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;
import com.manager.Difficulty;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HardcodedModel implements ILevelGenerationModel {
    private List<Pattern> hardPatterns;
    private Random random = new Random();

    @Override
    public void train(List<Pattern> sequence) {
        this.hardPatterns = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.HARD)
                .collect(Collectors.toList());
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        if (hardPatterns == null || hardPatterns.isEmpty()) {
            return PatternLibrary.getRandomPattern();
        }
        return hardPatterns.get(random.nextInt(hardPatterns.size()));
    }
}