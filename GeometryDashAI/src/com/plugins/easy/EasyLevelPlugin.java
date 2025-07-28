package com.plugins.easy;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;
import com.manager.Difficulty;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

public class EasyLevelPlugin implements ILevelGenerationModel {
    private List<Pattern> easyPatterns;
    private final Random rnd = new Random();

    @Override
    public void train(List<Pattern> sequence) {
        this.easyPatterns = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.EASY)
                .collect(Collectors.toList());
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        if (easyPatterns == null || easyPatterns.isEmpty()) {
            return PatternLibrary.getRandomPattern();
        }
        return easyPatterns.get(rnd.nextInt(easyPatterns.size()));
    }
}
