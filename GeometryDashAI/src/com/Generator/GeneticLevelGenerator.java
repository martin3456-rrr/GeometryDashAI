package com.Generator;

import com.Component.PlayerState;
import com.manager.Difficulty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GeneticLevelGenerator {

    private final int populationSize = 100;
    private final int targetLevelLength = 200;
    private final double mutationRate = 0.05;
    private final int tournamentSize = 5;
    private final int maxGenerations = 9999;

    private List<LevelChromosome> population;
    private final FitnessCalculator fitnessCalculator;

    // Hierarchiczne, specyficzne dla stanu łańcuchy Markowa 2. rzędu
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
        normalModeChain.train(PatternLibrary.PATTERNS);
        shipModeChain.train(PatternLibrary.PATTERNS);
        ballModeChain.train(PatternLibrary.PATTERNS);
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
            int currentLength = 0;

            PlayerState currentPlayerState = PlayerState.NORMAL;
            List<String> markovState = new ArrayList<>(List.of(MarkovChain.START_TOKEN, MarkovChain.START_TOKEN));

            while (currentLength < targetLevelLength) {
                // Określ docelową trudność na podstawie postępu w poziomie
                Difficulty targetDifficulty = getTargetDifficulty(currentLength, targetLevelLength);

                MarkovChain currentChain = getChainForState(currentPlayerState);

                Pattern nextPattern;
                int attempts = 0;
                // Pętla decyzyjna: znajdź wzorzec pasujący do kontekstu i docelowej trudności
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
    private Difficulty getTargetDifficulty(int currentLength, int totalLength) {
        double progress = (double) currentLength / totalLength;
        if (progress < 0.33) return Difficulty.EASY;
        if (progress < 0.66) return Difficulty.MEDIUM;
        return Difficulty.HARD;
    }

    private boolean isSequenceBad(List<Pattern> existingPatterns, Pattern nextPattern) {
        if (existingPatterns.isEmpty()) return false;

        Pattern lastPattern = existingPatterns.get(existingPatterns.size() - 1);

        // Reguła: Unikaj nagłych skoków trudności
        if (lastPattern.getDifficulty() == Difficulty.EASY && nextPattern.getDifficulty() == Difficulty.HARD) {
            return true;
        }
        if (lastPattern.getDifficulty() == Difficulty.HARD && nextPattern.getDifficulty() == Difficulty.EASY) {
            return true; // Unikaj też nagłego ułatwienia, chyba że to celowe
        }

        // Reguła: Unikaj portalu zaraz po innym portalu
        if (lastPattern.getName().contains("Portal") && nextPattern.getName().contains("Portal")) {
            return true;
        }
        return false;
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
        switch (state) {
            case FLYING:
                return shipModeChain;
            case BALL:
                return ballModeChain;
            default:
                return normalModeChain;
        }
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