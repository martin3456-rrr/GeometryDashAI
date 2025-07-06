package com.Generator;

import java.util.*;

public class FitnessCalculator {
    public void calculateFitness(LevelChromosome level) {
        double score = 0.0;
        score += checkPlayability(level);

        if (score == 0) {
            level.setFitness(0);
            return;
        }
        score += calculateDifficultyScore(level);
        score -= calculateMonotonyPenalty(level);
        score += calculateVarietyBonus(level);
        score += calculateFlowScore(level);
        level.setFitness(Math.max(0, score));
    }

    // **Kryterium 1: Grywalność - symulacja prostego bota**
    private double checkPlayability(LevelChromosome level) {
        List<GeneType> genes = level.getGenes();

        int playerHeight = 0;
        int jumpCooldown = 0;
        
        for (int i = 0; i < genes.size(); i++) {
            GeneType current = genes.get(i);
            
            // Zmniejsz cooldown skoku
            if (jumpCooldown > 0) jumpCooldown--;
            
            // Sprawdź czy gracz spada
            if (playerHeight > 0 && jumpCooldown == 0) {
                playerHeight--;
            }
            
            // Sprawdź kolizje
            switch (current) {
                case SPIKE_GROUND:
                    if (playerHeight == 0) {
                        if (i == 0 || jumpCooldown > 0) {
                            return 0;
                        }
                    }
                    break;
                    
                case BLOCK_GROUND:
                    if (playerHeight == 0) {
                        playerHeight = 1;
                        jumpCooldown = 3;
                    }
                    break;
                    
                case BLOCK_AIR, SPIKE_AIR:
                    if (playerHeight == 1) {
                        return 0;
                    }
                    break;
            }

            if (i < genes.size() - 2) {
                GeneType next = genes.get(i + 1);
                if ((next == GeneType.SPIKE_GROUND || next == GeneType.BLOCK_GROUND) && playerHeight == 0) {
                    playerHeight = 1;
                    jumpCooldown = 3;
                }
            }
        }
        
        return 200;
    }

    // **Kryterium 2: Poziom trudności**
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

    // **Kryterium 3: Różnorodność - kara za monotonię**
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

    // **Kryterium 3: Różnorodność - bonus za różnorodność**
    private double calculateVarietyBonus(LevelChromosome level) {
        List<GeneType> genes = level.getGenes();
        Set<GeneType> usedTypes = new HashSet<>(genes);

        double bonus = usedTypes.size() * 25;

        if (usedTypes.size() == GeneType.values().length) {
            bonus += 50;
        }
        
        return bonus;
    }

    // **Kryterium 4: Flow/Rytm**
    private double calculateFlowScore(LevelChromosome level) {
        double score = 0;
        List<GeneType> genes = level.getGenes();

        for (int i = 0; i < genes.size() - 2; i++) {
            GeneType current = genes.get(i);
            GeneType next = genes.get(i + 1);
            GeneType afterNext = genes.get(i + 2);

            if (isGoodSequence(current, next, afterNext)) {
                score += 10;
            }
            if (isBadSequence(current, next, afterNext)) {
                score -= 15;
            }
        }

        score += analyzeRhythm(genes);
        
        return score;
    }

    private boolean isGoodSequence(GeneType current, GeneType next, GeneType afterNext) {

        if (current == GeneType.EMPTY && next == GeneType.SPIKE_GROUND && afterNext == GeneType.EMPTY) {
            return true;
        }
        if (current == GeneType.EMPTY && next == GeneType.BLOCK_GROUND && afterNext == GeneType.EMPTY) {
            return true;
        }
        if (current == GeneType.SPIKE_GROUND && next == GeneType.EMPTY && afterNext == GeneType.EMPTY) {
            return true;
        }
        
        return false;
    }

    private boolean isBadSequence(GeneType current, GeneType next, GeneType afterNext) {
        
        if (current == GeneType.SPIKE_GROUND && next == GeneType.SPIKE_GROUND && afterNext == GeneType.SPIKE_GROUND) {
            return true;
        }
        if (current == GeneType.BLOCK_GROUND && next == GeneType.BLOCK_GROUND && afterNext == GeneType.BLOCK_GROUND) {
            return true;
        }
        if (current == GeneType.SPIKE_GROUND && next == GeneType.BLOCK_GROUND && afterNext == GeneType.SPIKE_GROUND) {
            return true;
        }
        
        return false;
    }

    private double analyzeRhythm(List<GeneType> genes) {
        double score = 0;
        List<Integer> obstaclePositions = new ArrayList<>();

        for (int i = 0; i < genes.size(); i++) {
            GeneType gene = genes.get(i);
            if (gene == GeneType.SPIKE_GROUND || gene == GeneType.BLOCK_GROUND || 
                gene == GeneType.SPIKE_AIR || gene == GeneType.BLOCK_AIR) {
                obstaclePositions.add(i);
            }
        }
        
        if (obstaclePositions.size() < 2) return 0;

        List<Integer> gaps = new ArrayList<>();
        for (int i = 1; i < obstaclePositions.size(); i++) {
            gaps.add(obstaclePositions.get(i) - obstaclePositions.get(i-1));
        }

        for (int gap : gaps) {
            if (gap >= 3 && gap <= 8) {
                score += 5;
            } else if (gap >= 2 && gap <= 10) {
                score += 2;
            } else if (gap == 1) {
                score -= 10;
            }
        }
        
        return score;
    }
}