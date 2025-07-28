package com.plugins;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;
import java.util.List;

public class SequenceModel implements ILevelGenerationModel {
    private List<Pattern> sequence;
    private int currentIndex = 0;

    @Override
    public void train(List<Pattern> trainingData) {
        this.sequence = List.of(
                PatternLibrary.getPatternByName("SimpleJump"),
                PatternLibrary.getPatternByName("BlockHop"),
                PatternLibrary.getPatternByName("ThreeSpikes")
        );
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        Pattern next = sequence.get(currentIndex);
        currentIndex = (currentIndex + 1) % sequence.size();
        return next;
    }
}