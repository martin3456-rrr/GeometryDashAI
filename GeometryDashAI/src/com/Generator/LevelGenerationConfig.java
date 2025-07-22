package com.Generator;

import com.manager.Difficulty;
import java.util.Set;

public class LevelGenerationConfig {
    private final Difficulty targetDifficulty;
    private final int targetLength;
    private final Set<GeneType> forbiddenMechanics;
    private final double funFactorWeight;
    private final GenerationModelType modelType;

    public LevelGenerationConfig(Difficulty difficulty, int length, Set<GeneType> forbidden, double funWeight,GenerationModelType modelType) {
        this.targetDifficulty = difficulty;
        this.targetLength = length;
        this.forbiddenMechanics = forbidden;
        this.funFactorWeight = funWeight;
        this.modelType = modelType;
    }

    public Difficulty getTargetDifficulty() { return targetDifficulty; }
    public int getTargetLength() { return targetLength; }
    public boolean isMechanicForbidden(GeneType type) {
        return forbiddenMechanics != null && forbiddenMechanics.contains(type);
    }
    public double getFunFactorWeight() { return funFactorWeight; }
}