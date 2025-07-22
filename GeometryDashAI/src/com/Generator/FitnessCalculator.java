package com.Generator;

import com.manager.Difficulty;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FitnessCalculator implements ILevelEvaluator {
    private static final double SCORE_PLAYABLE = 500.0;
    private static final double PENALTY_DEATH = -100.0;
    private static final double PENALTY_IMPOSSIBLE_SECTION = -1000.0;
    private static final double BONUS_VARIETY = 15.0;
    private static final double PENALTY_REPETITION = -75.0;
    private static final double BONUS_JUMP_SEQUENCES = 10.0;
    private static final double BONUS_MODE_TRANSITIONS = 20.0;
    private static final double BONUS_DYNAMIC_PATTERNS = 15.0;
    private static final double PENALTY_DIFFICULTY_SPIKE = -250.0;

    private final AdvancedPlaytester advancedPlaytester;

    public FitnessCalculator() {
        this.advancedPlaytester = new AdvancedPlaytester();
    }

    @Override
    public double evaluateFitness(LevelChromosome level, LevelGenerationConfig config) {
        ComprehensivePlaytestResult comprehensiveResult = advancedPlaytester.comprehensiveTest(level);

        int totalDeaths = comprehensiveResult.deaths;
        int totalImpossible = comprehensiveResult.impossibleSections;


        if (totalImpossible > 0 || totalDeaths > 2) {
            return Math.max(0, totalImpossible * PENALTY_IMPOSSIBLE_SECTION + totalDeaths * PENALTY_DEATH);
        }

        double score = SCORE_PLAYABLE;
        
        List<Pattern> patterns = level.getPatterns();
        score += calculateVarietyBonus(patterns);
        score -= calculateRepetitionPenalty(patterns);
        score -= calculateSpikePenalty(comprehensiveResult.deathHeatmap);
        score += calculateFunFactor(patterns) * config.getFunFactorWeight();
        score += calculateAdvancedFunFactor(patterns, config);

        level.setFitness(Math.max(0, score));
        return score;
    }


    private double calculateFunFactor(List<Pattern> patterns) {
        double funFactor = 0.0;
        funFactor += calculateJumpSequenceBonus(patterns);
        funFactor += calculateModeTransitionBonus(patterns);
        funFactor += calculateDynamicPatternBonus(patterns);
        
        return funFactor;
    }
    private double calculateJumpSequenceBonus(List<Pattern> patterns) {
        double bonus = 0.0;
        int consecutiveJumps = 0;
        
        for (Pattern pattern : patterns) {
            if (pattern.getName().toLowerCase().contains("jump") || 
                pattern.getName().toLowerCase().contains("spike")) {
                consecutiveJumps++;
                if (consecutiveJumps >= 3) {
                    bonus += BONUS_JUMP_SEQUENCES;
                }
            } else {
                consecutiveJumps = 0;
            }
        }
        
        return bonus;
    }
    
    private double calculateModeTransitionBonus(List<Pattern> patterns) {
        double bonus = 0.0;
        
        for (Pattern pattern : patterns) {
            if (pattern.getName().toLowerCase().contains("portal") ||
                pattern.getName().toLowerCase().contains("ship") ||
                pattern.getName().toLowerCase().contains("ball")) {
                bonus += BONUS_MODE_TRANSITIONS;
            }
        }
        
        return bonus;
    }
    
    private double calculateDynamicPatternBonus(List<Pattern> patterns) {
        double bonus = 0.0;
        
        for (Pattern pattern : patterns) {
            if (pattern.getName().toLowerCase().contains("moving") ||
                pattern.getName().toLowerCase().contains("dynamic") ||
                pattern.getName().toLowerCase().contains("wave")) {
                bonus += BONUS_DYNAMIC_PATTERNS;
            }
        }
        
        return bonus;
    }

    private double calculateRepetitionPenalty(List<Pattern> patterns) {
        double penalty = 0.0;
        if (patterns.size() < 2) return 0;

        for (int i = 0; i < patterns.size() - 1; i++) {
            if (patterns.get(i).getName().equals(patterns.get(i + 1).getName())) {
                penalty += PENALTY_REPETITION;
            }
        }
        return penalty;
    }

    private double calculateVarietyBonus(List<Pattern> patterns) {
        Set<String> uniquePatterns = new HashSet<>();
        for (Pattern p : patterns) {
            uniquePatterns.add(p.getName());
        }
        return uniquePatterns.size() * BONUS_VARIETY;
    }
    private double calculateSpikePenalty(Map<Integer, Integer> heatmap) {
        if (heatmap.isEmpty()) {
            return 0;
        }
        int maxDeathsInOneSpot = 0;
        for (int count : heatmap.values()) {
            if (count > maxDeathsInOneSpot) {
                maxDeathsInOneSpot = count;
            }
        }

        if (maxDeathsInOneSpot > 1) {
            return Math.pow(maxDeathsInOneSpot, 2) * PENALTY_DIFFICULTY_SPIKE;
        }
        return 0;
    }

    private double calculateAdvancedFunFactor(List<Pattern> patterns, LevelGenerationConfig config) {
        double funFactor = 0.0;

        funFactor += calculateRhythmCoherence(patterns);
        funFactor += calculateChallengeBalance(patterns);
        funFactor += calculateDifficultyProgression(patterns);
        funFactor += calculateMechanicVariation(patterns);

        return funFactor * config.getFunFactorWeight();
    }
    private double calculateRhythmCoherence(List<Pattern> patterns) {
        long jumpLikePatterns = patterns.stream()
                .filter(p -> p.getName().toLowerCase().contains("jump") || p.getName().toLowerCase().contains("spike"))
                .count();
        double ratio = (double) jumpLikePatterns / patterns.size();
        return (ratio > 0.4 && ratio < 0.6) ? 20.0 : -10.0;
    }

    private double calculateChallengeBalance(List<Pattern> patterns) {
        long easy = patterns.stream().filter(p -> p.getDifficulty() == Difficulty.EASY).count();
        long medium = patterns.stream().filter(p -> p.getDifficulty() == Difficulty.MEDIUM).count();
        long hard = patterns.stream().filter(p -> p.getDifficulty() == Difficulty.HARD).count();
        if (easy > patterns.size() * 0.8 || medium > patterns.size()*0.5 || hard > patterns.size() * 0.7) {
            return -50.0;
        }
        return 15.0;
    }

    private double calculateDifficultyProgression(List<Pattern> patterns) {
        if (patterns.size() < 10) return 0.0;
        int increasingSegments = 0;
        for (int i = 0; i < patterns.size() - 1; i++) {
            if (patterns.get(i).getDifficulty().ordinal() < patterns.get(i + 1).getDifficulty().ordinal()) {
                increasingSegments++;
            }
        }
        return increasingSegments * 2.0;
    }
    private double calculateMechanicVariation(List<Pattern> patterns) {
        Set<GeneType> mechanics = new HashSet<>();
        for (Pattern pattern : patterns) {
            pattern.getGenes().forEach(gene -> {
                if (gene.toString().startsWith("PORTAL")) {
                    mechanics.add(gene);
                }
            });
        }
        return mechanics.size() * 25.0;
    }

}