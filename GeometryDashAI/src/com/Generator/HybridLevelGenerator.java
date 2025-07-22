package com.Generator;

import com.Component.PlayerState;

import java.util.List;
import java.util.Map;

class GenerationContext {
    public final double desiredDifficulty;
    public final PlayerState currentState;
    public final int levelProgress;

    public GenerationContext(double desiredDifficulty, PlayerState currentState, int levelProgress) {
        this.desiredDifficulty = desiredDifficulty;
        this.currentState = currentState;
        this.levelProgress = levelProgress;
    }
}

public class HybridLevelGenerator {
    private final MarkovChain markovChain;
    private final LstmLevelGenerator lstmGenerator;
    private boolean hasTrainingData = false;

    public HybridLevelGenerator() {
        this.markovChain = new MarkovChain();
        this.lstmGenerator = new LstmLevelGenerator();
        Map<String, List<String>> originalLevels = PatternLibrary.getOriginalLevels();
        for (Map.Entry<String, List<String>> levelEntry : originalLevels.entrySet()) {
            List<String> patternNames = levelEntry.getValue();
            if (patternNames != null && !patternNames.isEmpty()) {
                List<Pattern> levelPatterns = PatternLibrary.getPatternsFromNames(patternNames);
                this.markovChain.train(levelPatterns);
                hasTrainingData = true;
            }
        }

        List<Pattern> trainingData = TrainingDataAggregator.loadAllLevelsFromDirectory("data/community_levels");
        if (!trainingData.isEmpty()) {
            this.markovChain.train(trainingData);
            this.lstmGenerator.train(trainingData);
            hasTrainingData = true;
        }
    }

    public Pattern getNextPattern(List<String> currentState, GenerationContext context) {
        Pattern suggestion = null;

        if (hasTrainingData) {
            Pattern markovSuggestion = markovChain.getNextPattern(currentState);
            Pattern lstmSuggestion = lstmGenerator.getNextPattern(currentState);
            suggestion = selectBestPattern(lstmSuggestion, markovSuggestion, null, context);
        }

        if (suggestion == null) {
            suggestion = PatternLibrary.getRandomPattern();
        }

        return suggestion;
    }

    private Pattern selectBestPattern(Pattern lstm, Pattern markov, Pattern random, GenerationContext context) {
        if (context.levelProgress < 50) {
            return markov.getDifficulty().ordinal() <= context.desiredDifficulty ? markov : lstm;
        } else {
            return lstm.getDifficulty().ordinal() <= context.desiredDifficulty ? lstm : random;
        }
    }
}