package com.plugins.random;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;

import java.util.List;
import java.util.Random;

public class RandomPatternPlugin implements ILevelGenerationModel {
    private List<Pattern> allPatterns;
    private final Random rnd = new Random();

    @Override
    public void train(List<Pattern> sequence) {
        this.allPatterns = PatternLibrary.PATTERNS;
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        return allPatterns.get(rnd.nextInt(allPatterns.size()));
    }
}
