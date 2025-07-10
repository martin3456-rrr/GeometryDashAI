package com.Generator;

import com.Component.PlayerState;
import com.manager.Difficulty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

enum LevelTheme {
    JUMPING_SECTION,
    SHIP_SECTION,
    BALL_SECTION
}
public class GeneticLevelGenerator {

    private final int populationSize = 100;
    private final int targetLevelLength = 200;
    private final double mutationRate = 0.05;
    private final int tournamentSize = 5;
    private final int maxGenerations = 9999;

    private List<LevelChromosome> population;
    private final FitnessCalculator fitnessCalculator;

    private final MarkovChain normalModeChain;
    private final MarkovChain shipModeChain;
    private final MarkovChain ballModeChain;

    public GeneticLevelGenerator() {
        this.normalModeChain = new MarkovChain();
        this.shipModeChain = new MarkovChain();
        this.ballModeChain = new MarkovChain();

        trainModels();

        this.fitnessCalculator = new FitnessCalculator(Map.of(
                PlayerState.NORMAL, normalModeChain,
                PlayerState.FLYING, shipModeChain,
                PlayerState.BALL, ballModeChain
        ));

        this.population = initializePopulation();
    }

    private void trainModels() {
        Map<String, List<String>> originalLevels = PatternLibrary.getOriginalLevels();
        List<String> stereoMadnessPatternNames = originalLevels.get("stereo_madness");

        if (stereoMadnessPatternNames != null) {
            List<Pattern> stereoMadnessPatterns = PatternLibrary.getPatternsFromNames(stereoMadnessPatternNames);
            normalModeChain.train(stereoMadnessPatterns);
            shipModeChain.train(stereoMadnessPatterns);
            ballModeChain.train(stereoMadnessPatterns);
        } else {
            normalModeChain.train(PatternLibrary.PATTERNS);
            shipModeChain.train(PatternLibrary.PATTERNS);
            ballModeChain.train(PatternLibrary.PATTERNS);
        }
    }
    public LevelChromosome generateBestLevel() {
        for (int generation = 0; generation < maxGenerations; generation++) {
            for (LevelChromosome level : population) {
                fitnessCalculator.calculateFitness(level);
            }
            population.sort(Comparator.comparingDouble(LevelChromosome::getFitness).reversed());

            List<LevelChromosome> newPopulation = new ArrayList<>();
            int eliteSize = (int) (populationSize * 0.1);
            newPopulation.addAll(population.subList(0, eliteSize));

            for (int i = eliteSize; i < populationSize; i++) {
                LevelChromosome parent1 = tournamentSelection();
                LevelChromosome parent2 = tournamentSelection();
                LevelChromosome offspring = crossover(parent1, parent2);
                mutate(offspring);
                newPopulation.add(offspring);
            }
            population = newPopulation;
        }

        return population.stream()
                .max(Comparator.comparing(LevelChromosome::getFitness))
                .orElse(null);
    }

    private List<LevelChromosome> initializePopulation() {
        List<LevelChromosome> newPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Pattern> chromosomePatterns = new ArrayList<>();

            List<LevelTheme> levelPlan = generateLevelPlan();

            for (LevelTheme theme : levelPlan) {
                chromosomePatterns.addAll(fillThemeWithPatterns(theme, targetLevelLength / levelPlan.size()));
            }
            int currentLength = 0;

            PlayerState currentPlayerState = PlayerState.NORMAL;
            List<String> markovState = new ArrayList<>(List.of(MarkovChain.START_TOKEN, MarkovChain.START_TOKEN));

            while (currentLength < targetLevelLength) {
                Difficulty targetDifficulty = getTargetDifficulty(currentLength, targetLevelLength);
                MarkovChain currentChain = getChainForState(currentPlayerState);
                Pattern nextPattern;
                int attempts = 0;
                do {
                    nextPattern = currentChain.getNextPattern(markovState);
                    attempts++;
                } while ((isSequenceBad(chromosomePatterns, nextPattern) || nextPattern.getDifficulty() != targetDifficulty) && attempts < 20);

                chromosomePatterns.add(nextPattern);
                currentLength += nextPattern.getLength();
                markovState.remove(0);
                markovState.add(nextPattern.getName());
                currentPlayerState = getPlayerStateAfterPattern(nextPattern, currentPlayerState);
            }
            newPopulation.add(new LevelChromosome(chromosomePatterns));
        }
        return newPopulation;
    }
    private List<LevelTheme> generateLevelPlan() {
        List<LevelTheme> plan = new ArrayList<>();
        plan.add(LevelTheme.JUMPING_SECTION);
        plan.add(LevelTheme.SHIP_SECTION);
        plan.add(LevelTheme.JUMPING_SECTION);
        plan.add(LevelTheme.BALL_SECTION);
        return plan;
    }

    private List<Pattern> fillThemeWithPatterns(LevelTheme theme, int targetLength) {
        List<Pattern> segmentPatterns = new ArrayList<>();
        int currentLength = 0;

        PlayerState stateForTheme;
        MarkovChain chainForTheme;

        switch (theme) {
            case SHIP_SECTION:
                stateForTheme = PlayerState.FLYING;
                chainForTheme = this.shipModeChain;
                segmentPatterns.add(PatternLibrary.getPatternByName("PortalToShip"));
                break;
            case BALL_SECTION:
                stateForTheme = PlayerState.BALL;
                chainForTheme = this.ballModeChain;
                segmentPatterns.add(PatternLibrary.getPatternByName("PortalToBall"));
                break;
            default:
                stateForTheme = PlayerState.NORMAL;
                chainForTheme = this.normalModeChain;
                break;
        }

        List<String> markovState = new ArrayList<>(List.of(MarkovChain.START_TOKEN, MarkovChain.START_TOKEN));

        while (currentLength < targetLength) {
            Pattern nextPattern = chainForTheme.getNextPattern(markovState);
            if (isPatternValidForState(nextPattern, stateForTheme)) {
                segmentPatterns.add(nextPattern);
                currentLength += nextPattern.getLength();
                markovState.remove(0);
                markovState.add(nextPattern.getName());
            }
        }
        if (theme != LevelTheme.JUMPING_SECTION) {
            segmentPatterns.add(PatternLibrary.getPatternByName("PortalToNormal"));
        }

        return segmentPatterns;
    }

    private boolean isPatternValidForState(Pattern pattern, PlayerState state) {
        boolean hasPortal = pattern.getGenes().stream().anyMatch(g -> g.name().contains("PORTAL"));
        if (state == PlayerState.NORMAL) {
            return !hasPortal && !pattern.getName().contains("Ship") && !pattern.getName().contains("Ball");
        }
        return true;
    }
    private Difficulty getTargetDifficulty(int currentLength, int totalLength) {
        double progress = (double) currentLength / totalLength;
        if (progress < 0.33) return Difficulty.EASY;
        if (progress < 0.66) return Difficulty.MEDIUM;
        return Difficulty.HARD;
    }

    private boolean isSequenceBad(List<Pattern> existingPatterns, Pattern nextPattern) {
        if (existingPatterns.isEmpty()) return false;
        Pattern lastPattern = existingPatterns.getLast();
        if (lastPattern.getDifficulty() == Difficulty.EASY && nextPattern.getDifficulty() == Difficulty.HARD) {
            return true;
        }
        if (lastPattern.getDifficulty() == Difficulty.HARD && nextPattern.getDifficulty() == Difficulty.EASY) {
            return true;
        }
        return lastPattern.getName().contains("Portal") && nextPattern.getName().contains("Portal");
    }

    private void mutate(LevelChromosome level) {
        List<Pattern> patterns = level.getPatterns();
        for (int i = 0; i < patterns.size(); i++) {
            if (Math.random() < mutationRate) {
                PlayerState stateAtMutationPoint = PlayerState.NORMAL;
                List<String> markovStateAtMutationPoint = new ArrayList<>(List.of(MarkovChain.START_TOKEN, MarkovChain.START_TOKEN));

                if (i > 1) {
                    for(int j=0; j<i-1; j++) {
                        stateAtMutationPoint = getPlayerStateAfterPattern(patterns.get(j), stateAtMutationPoint);
                    }
                    markovStateAtMutationPoint = List.of(patterns.get(i-2).getName(), patterns.get(i-1).getName());
                } else if (i == 1) {
                    markovStateAtMutationPoint = List.of(MarkovChain.START_TOKEN, patterns.get(0).getName());
                }

                MarkovChain currentChain = getChainForState(stateAtMutationPoint);
                patterns.set(i, currentChain.getNextPattern(markovStateAtMutationPoint));
            }
        }
    }

    private MarkovChain getChainForState(PlayerState state) {
        return switch (state) {
            case FLYING -> shipModeChain;
            case BALL -> ballModeChain;
            default -> normalModeChain;
        };
    }

    private PlayerState getPlayerStateAfterPattern(Pattern pattern, PlayerState currentState) {
        for (GeneType gene : pattern.getGenes()) {
            switch (gene) {
                case PORTAL_SHIP: return PlayerState.FLYING;
                case PORTAL_BALL: return PlayerState.BALL;
                case PORTAL_NORMAL: return PlayerState.NORMAL;
            }
        }
        return currentState;
    }

    private LevelChromosome tournamentSelection() {
        List<LevelChromosome> tournament = new ArrayList<>();
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = (int) (Math.random() * population.size());
            tournament.add(population.get(randomIndex));
        }
        return tournament.stream().max(Comparator.comparing(LevelChromosome::getFitness)).orElse(null);
    }

    private LevelChromosome crossover(LevelChromosome parent1, LevelChromosome parent2) {
        List<Pattern> parent1Patterns = parent1.getPatterns();
        List<Pattern> parent2Patterns = parent2.getPatterns();
        int crossoverPoint = (int) (Math.random() * Math.min(parent1Patterns.size(), parent2Patterns.size()));
        List<Pattern> offspringPatterns = new ArrayList<>();
        offspringPatterns.addAll(parent1Patterns.subList(0, crossoverPoint));
        offspringPatterns.addAll(parent2Patterns.subList(crossoverPoint, parent2Patterns.size()));
        return new LevelChromosome(offspringPatterns);
    }
}