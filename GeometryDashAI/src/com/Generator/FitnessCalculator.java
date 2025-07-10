package com.Generator;

import com.Component.PlayerState;

import java.util.*;

public class FitnessCalculator {
    private static final double PLAYABILITY_SCORE = 200.0;
    private static final double PENALTY_DEATH = -50.0;
    private static final double MARKOV_FLOW_BONUS = 75.0; // Zwiększony bonus
    private final Map<PlayerState, MarkovChain> chains;
    public FitnessCalculator(Map<PlayerState, MarkovChain> chains) {
        this.chains = chains;
    }

    public void calculateFitness(LevelChromosome level) {
        double score = 0.0;
        score += checkPlayability(level);

        if (score <= 0) {
            level.setFitness(0);
            return;
        }

        score += calculateDifficultyScore(level);
        score -= calculateMonotonyPenalty(level);
        score += calculateVarietyBonus(level);
        score += calculateFlowScore(level);
        score += calculateMarkovFlowBonus(level);
        score += rewardForOriginalFlow(level);
        score -= penalizePatternRepetition(level);

        level.setFitness(Math.max(0, score));
    }
    private double rewardForOriginalFlow(LevelChromosome level) {
        double bonus = 0.0;
        List<Pattern> patterns = level.getPatterns();
        List<String> originalSequence = PatternLibrary.getOriginalLevels().get("stereo_madness");

        if (originalSequence == null || patterns.size() < 2) {
            return 0;
        }

        for (int i = 0; i < patterns.size() - 1; i++) {
            String p1 = patterns.get(i).getName();
            String p2 = patterns.get(i + 1).getName();
            for (int j = 0; j < originalSequence.size() - 1; j++) {
                if (originalSequence.get(j).equals(p1) && originalSequence.get(j + 1).equals(p2)) {
                    bonus += 20.0;
                    break;
                }
            }
        }
        return bonus;
    }

    private double penalizePatternRepetition(LevelChromosome level) {
        double penalty = 0.0;
        List<Pattern> patterns = level.getPatterns();
        if (patterns.size() < 2) return 0;

        for (int i = 0; i < patterns.size() - 1; i++) {
            if (patterns.get(i).getName().equals(patterns.get(i+1).getName())) {
                penalty += 30.0;
            }
        }
        return penalty;
    }
    private double calculateMarkovFlowBonus(LevelChromosome level) {
        double bonus = 0.0;
        List<Pattern> patterns = level.getPatterns();
        if (patterns.size() < 3) return 0;

        PlayerState currentState = PlayerState.NORMAL;
        MarkovChain currentChain = chains.get(currentState);

        List<String> markovState = List.of(MarkovChain.START_TOKEN, patterns.get(0).getName());
        if (currentChain.isKnownTransition(markovState, patterns.get(1).getName())) {
            bonus += 5;
        }

        for (int i = 0; i < patterns.size() - 2; i++) {
            Pattern p1 = patterns.get(i);
            Pattern p2 = patterns.get(i + 1);
            Pattern p3 = patterns.get(i + 2);

            currentState = getPlayerStateAfterPattern(p1, currentState);
            currentChain = chains.get(currentState);
            markovState = List.of(p1.getName(), p2.getName());

            if (currentChain.isKnownTransition(markovState, p3.getName())) {
                bonus += 5;
            }
        }
        return Math.min(bonus, MARKOV_FLOW_BONUS);
    }

    private PlayerState getPlayerStateAfterPattern(Pattern pattern, PlayerState currentState) {
        for (GeneType gene : pattern.getGenes()) {
            switch(gene) {
                case PORTAL_SHIP: return PlayerState.FLYING;
                case PORTAL_BALL: return PlayerState.BALL;
                case PORTAL_NORMAL: return PlayerState.NORMAL;
            }
        }
        return currentState;
    }
    private double checkPlayability(LevelChromosome level) {
        List<GeneType> genes = level.getGenes();
        PlayerState botState = PlayerState.NORMAL;
        double botY = 0;
        double botVelocityY = 0;
        float gravityMultiplier = 1.0f;
        for (int i = 0; i < genes.size(); i++) {
            GeneType currentGene = genes.get(i);
            GeneType nextGene = (i + 1 < genes.size()) ? genes.get(i + 1) : GeneType.EMPTY;
            if (botState == PlayerState.NORMAL) {
                if (botY > 0) {
                    botY -= 0.5;
                }
            } else if (botState == PlayerState.FLYING) {
                botVelocityY -= 0.1;
                botY += botVelocityY;
                if (botY < 0) botY = 0;
            }

            switch (botState) {
                case NORMAL:
                    if ((nextGene == GeneType.SPIKE_GROUND || nextGene == GeneType.BLOCK_GROUND) && botY == 0) {
                        botY = 1;
                    }
                    break;
                case FLYING:
                    if ((nextGene == GeneType.SPIKE_AIR || nextGene == GeneType.BLOCK_FLOATING) && botY < 1.5) {
                        botVelocityY += 0.3;
                    }
                    if (botY > 3.0) return PENALTY_DEATH;
                    break;
                case BALL:
                    if ((nextGene == GeneType.SPIKE_GROUND && gravityMultiplier == -1.0f) || (nextGene == GeneType.SPIKE_AIR && gravityMultiplier == 1.0f)) {
                        gravityMultiplier *= -1;
                    }
                    break;
            }

            switch (currentGene) {
                case SPIKE_GROUND:
                    if (botY == 0 && gravityMultiplier == 1.0f) return PENALTY_DEATH;
                    break;
                case SPIKE_AIR:
                    if (botY > 0 && gravityMultiplier == -1.0f) return PENALTY_DEATH;
                    break;
                case PORTAL_SHIP:
                    botState = PlayerState.FLYING;
                    botVelocityY = 0;
                    break;
                case PORTAL_BALL:
                    botState = PlayerState.BALL;
                    break;
                case PORTAL_NORMAL:
                    botState = PlayerState.NORMAL;
                    gravityMultiplier = 1.0f;
                    break;
            }
        }
        return PLAYABILITY_SCORE;
    }

    private double calculateDifficultyScore(LevelChromosome level) {
        double score = 0;
        List<GeneType> genes = level.getGenes();

        int spikeCount = (int) genes.stream().filter(g ->
                g == GeneType.SPIKE_GROUND || g == GeneType.SPIKE_AIR).count();
        int blockCount = (int) genes.stream().filter(g ->
                g == GeneType.BLOCK_GROUND || g == GeneType.BLOCK_AIR).count();
        int emptyCount = (int) genes.stream().filter(g -> g == GeneType.EMPTY).count();

        double obstacleRatio = (double)(spikeCount + blockCount) / genes.size();
        if (obstacleRatio >= 0.1 && obstacleRatio <= 0.25) {
            score += 150;
        } else if (obstacleRatio >= 0.05 && obstacleRatio <= 0.35) {
            score += 100;
        } else {
            score += 50;
        }

        if (spikeCount >= 5 && spikeCount <= 20) {
            score += 100;
        }
        double emptyRatio = (double)emptyCount / genes.size();
        if (emptyRatio >= 0.6 && emptyRatio <= 0.8) {
            score += 50;
        }

        return score;
    }

    private double calculateMonotonyPenalty(LevelChromosome level) {
        double penalty = 0;
        List<GeneType> genes = level.getGenes();

        int currentStreak = 1;
        GeneType currentType = genes.get(0);
        for (int i = 1; i < genes.size(); i++) {
            if (genes.get(i) == currentType) {
                currentStreak++;
            } else {
                if (currentStreak >= 15) {
                    penalty += (currentStreak - 14) * 10;
                }
                if (currentStreak >= 25) {
                    penalty += (currentStreak - 24) * 20;
                }

                currentStreak = 1;
                currentType = genes.get(i);
            }
        }

        if (currentStreak >= 15) {
            penalty += (currentStreak - 14) * 10;
        }
        if (currentStreak >= 25) {
            penalty += (currentStreak - 24) * 20;
        }

        return penalty;
    }
    private double calculateVarietyBonus(LevelChromosome level) {
        List<GeneType> genes = level.getGenes();
        Set<GeneType> usedTypes = new HashSet<>(genes);

        double bonus = usedTypes.size() * 25;
        if (usedTypes.size() == GeneType.values().length) {
            bonus += 50;
        }

        return bonus;
    }
    private double calculateFlowScore(LevelChromosome level) {
        double score = 0;
        List<GeneType> genes = level.getGenes();
        PlayerState currentState = PlayerState.NORMAL;

        for (int i = 0; i < genes.size() - 2; i++) {
            GeneType current = genes.get(i);
            GeneType next = genes.get(i + 1);
            GeneType afterNext = genes.get(i + 2);
            if (current == GeneType.PORTAL_SHIP) currentState = PlayerState.FLYING;
            if (current == GeneType.PORTAL_NORMAL) currentState = PlayerState.NORMAL;

            if (isGoodSequence(current, next, afterNext, currentState)) {
                score += 10;
            }
            if (isBadSequence(current, next, afterNext, currentState)) {
                score -= 15;
            }
        }
        return score;
    }

    private boolean isGoodSequence(GeneType current, GeneType next, GeneType afterNext,PlayerState state) {
        if (state == PlayerState.NORMAL) {
            if (current == GeneType.EMPTY && next == GeneType.SPIKE_GROUND && afterNext == GeneType.EMPTY) return true;
        }
        if (state == PlayerState.FLYING) {
            if (current == GeneType.EMPTY && next == GeneType.BLOCK_FLOATING && afterNext == GeneType.EMPTY) return true;
        }
        return false;
    }

    private boolean isBadSequence(GeneType current, GeneType next, GeneType afterNext, PlayerState state) {
        if (current == GeneType.SPIKE_GROUND && next == GeneType.SPIKE_GROUND && afterNext == GeneType.SPIKE_GROUND) return true;
        return false;
    }
}