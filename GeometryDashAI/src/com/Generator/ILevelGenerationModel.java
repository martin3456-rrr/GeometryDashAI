package com.Generator;

import java.util.List;


public interface ILevelGenerationModel {
    void train(List<Pattern> sequence);
    Pattern getNextPattern(List<String> currentState);
}