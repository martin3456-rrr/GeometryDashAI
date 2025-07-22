package com.Generator;

import java.util.List;
import java.util.Random;

public class RepetitiveModel implements ILevelGenerationModel {
    private Pattern patternToRepeat;

    @Override
    public void train(List<Pattern> sequence) {
        if (sequence != null && !sequence.isEmpty()) {
            this.patternToRepeat = sequence.get(new Random().nextInt(sequence.size()));
        } else {
            this.patternToRepeat = PatternLibrary.getRandomPattern();
        }
        System.out.println("RepetitiveModel będzie powtarzać wzorzec: " + patternToRepeat.getName());
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        return patternToRepeat;
    }
}