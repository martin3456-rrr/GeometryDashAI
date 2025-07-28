package com.plugins.progressive;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;
import com.manager.Difficulty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

public class ProgressiveDifficultyPlugin implements ILevelGenerationModel {
    private List<Pattern> easy, medium, hard;
    private int counter = 0;
    private final Random rnd = new Random();

    @Override
    public void train(List<Pattern> sequence) {
        easy   = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.EASY)
                .collect(Collectors.toList());
        medium = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.MEDIUM)
                .collect(Collectors.toList());
        hard   = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.HARD)
                .collect(Collectors.toList());
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        counter++;
        if (counter < 5 && !easy.isEmpty()) {
            return easy.get(rnd.nextInt(easy.size()));
        } else if (counter < 10 && !medium.isEmpty()) {
            return medium.get(rnd.nextInt(medium.size()));
        } else if (!hard.isEmpty()) {
            return hard.get(rnd.nextInt(hard.size()));
        } else {
            return PatternLibrary.getRandomPattern();
        }
    }
}
