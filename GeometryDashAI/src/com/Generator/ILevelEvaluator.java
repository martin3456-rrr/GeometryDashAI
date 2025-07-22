package com.Generator;

public interface ILevelEvaluator {
    double evaluateFitness(LevelChromosome level, LevelGenerationConfig config);
}