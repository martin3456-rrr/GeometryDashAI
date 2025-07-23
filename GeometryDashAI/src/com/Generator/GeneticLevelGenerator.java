package com.Generator;

import com.Component.PlayerState;

import java.util.*;

import com.jade.Log;
import com.manager.Difficulty;
import com.manager.PluginLoader;

public class GeneticLevelGenerator {

    private final int populationSize;
    private final double mutationRate;
    private final int tournamentSize;
    private final int maxGenerations;
    private final AIPlaytester playtesterForMutation = new AIPlaytester();
    private final ILevelGenerationModel lstmModel;
    private final HybridLevelGenerator hybridGenerator;
    private final Random parameterRandom = new Random();

    private List<LevelChromosome> population;
    private final ILevelEvaluator fitnessCalculator;
    private final Map<PlayerState, ILevelGenerationModel> generationModels;
    private final List<ILevelGenerationModel> pluginModels;

    public GeneticLevelGenerator() {
        this.populationSize = generateRandomPopulationSize();
        this.mutationRate = generateRandomMutationRate();
        this.tournamentSize = generateRandomTournamentSize();
        this.maxGenerations = generateRandomMaxGenerations();

        ILevelGenerationModel normalModeChain = new MarkovChain();
        ILevelGenerationModel shipModeChain = new MarkovChain();
        ILevelGenerationModel ballModeChain = new MarkovChain();
        this.hybridGenerator = new HybridLevelGenerator();

        this.generationModels = Map.of(
                PlayerState.NORMAL, normalModeChain,
                PlayerState.FLYING, shipModeChain,
                PlayerState.BALL, ballModeChain
        );

        trainModels();

        this.lstmModel = new LstmLevelGenerator();
        System.out.println("Rozpoczynanie wczytywania danych dla LSTM...");
        List<Pattern> trainingData = TrainingDataAggregator.loadAllLevelsFromDirectory("data/community_levels"); // Użycie TrainingDataAggregator
        if (!trainingData.isEmpty()) {
            this.lstmModel.train(trainingData);
        } else {
            System.err.println("Brak danych treningowych dla LSTM, model nie będzie w pełni funkcjonalny.");
        }

        System.out.println("Wczytywanie zewnętrznych modeli generowania (pluginów)...");
        this.pluginModels = PluginLoader.loadPlugins("plugins", ILevelGenerationModel.class);
        if (this.pluginModels.isEmpty()) {
            System.out.println("Nie znaleziono żadnych pluginów.");
        }

        this.fitnessCalculator = new FitnessCalculator();
    }

    private void trainModels() {
        Map<String, List<String>> originalLevels = PatternLibrary.getOriginalLevels();
        for (Map.Entry<String, List<String>> levelEntry : originalLevels.entrySet()) {
            List<String> patternNames = levelEntry.getValue();
            
            if (patternNames != null && !patternNames.isEmpty()) {
                List<Pattern> levelPatterns = PatternLibrary.getPatternsFromNames(patternNames);
                generationModels.get(PlayerState.NORMAL).train(levelPatterns);
                generationModels.get(PlayerState.FLYING).train(levelPatterns);
                generationModels.get(PlayerState.BALL).train(levelPatterns);
            }
        }
    }
    public LevelChromosome generateBestLevel(LevelGenerationConfig config) {
        this.population = initializePopulation(config);

        for (int generation = 0; generation < maxGenerations; generation++) {
            for (LevelChromosome level : population) {
                fitnessCalculator.evaluateFitness(level, config);
            }
            population.sort(Comparator.comparingDouble(LevelChromosome::getFitness).reversed());

            if (generation % 10 == 0) {
                Log.add("Generacja " + generation + ": Najlepszy Fitness = " + population.getFirst().getFitness());
                Log.add("Najlepszy poziom ma " + population.getFirst().getPatterns().size() + " wzorców");
            }

            int eliteSize = (int) (populationSize * 0.05);
            List<LevelChromosome> newPopulation = new ArrayList<>(population.subList(0, eliteSize));

            for (int i = eliteSize; i < populationSize; i++) {
                LevelChromosome parent1 = tournamentSelection();
                LevelChromosome parent2 = tournamentSelection();
                LevelChromosome offspring = crossover(parent1, parent2);
                mutate(offspring);
                newPopulation.add(offspring);
            }
            population = newPopulation;
        }

        LevelChromosome bestLevel = population.stream()
                .max(Comparator.comparing(LevelChromosome::getFitness))
                .orElse(null);
            
        if (bestLevel != null) {
            Log.add("Wygenerowano najlepszy poziom z " + bestLevel.getPatterns().size() + " wzorców");
        }
        
        return bestLevel;
    }

    private List<LevelChromosome> initializePopulation(LevelGenerationConfig config) {
        List<LevelChromosome> newPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            List<Pattern> chromosomePatterns = new ArrayList<>();
            PlayerState currentPlayerState = PlayerState.NORMAL;
            List<String> history = new ArrayList<>(List.of(MarkovChain.START_TOKEN, MarkovChain.START_TOKEN));
            int currentLength = 0;
            int maxAttempts = config.getTargetLength() * 5;
            int attempts = 0;
            int consecutiveFailures = 0;

            while (currentLength < config.getTargetLength() && attempts < maxAttempts) {
                attempts++;
                GenerationContext context = new GenerationContext(
                        config.getTargetDifficulty().ordinal(),
                        currentPlayerState,
                        (int)((double)currentLength / config.getTargetLength() * 100)
                );

                Pattern nextPattern = hybridGenerator.getNextPattern(history, context);

                if (nextPattern == null) {
                    consecutiveFailures++;
                    if (consecutiveFailures > 3) {
                        nextPattern = PatternLibrary.getRandomPattern();
                        consecutiveFailures = 0;
                    }
                    if (nextPattern == null) {
                        nextPattern = new Pattern("Emergency", List.of(GeneType.EMPTY, GeneType.BLOCK_GROUND), Difficulty.EASY);
                    }
                } else {
                    consecutiveFailures = 0;
                }

                chromosomePatterns.add(nextPattern);
                currentLength += nextPattern.getLength();
                history.add(nextPattern.getName());
                if (history.size() > 2) history.removeFirst();
                currentPlayerState = getPlayerStateAfterPattern(nextPattern, currentPlayerState);
            }

            while (currentLength < config.getTargetLength() * 0.8) {
                Pattern fillerPattern = getSimpleFillerPattern(currentPlayerState);
                chromosomePatterns.add(fillerPattern);
                currentLength += fillerPattern.getLength();
                currentPlayerState = getPlayerStateAfterPattern(fillerPattern, currentPlayerState);
            }

            Log.add("Chromosome " + i + " created with " + chromosomePatterns.size() +
                    " patterns, total length: " + currentLength + " (target: " + config.getTargetLength() + ")");
            newPopulation.add(new LevelChromosome(chromosomePatterns));
        }
        return newPopulation;
    }

    private Pattern getSimpleFillerPattern(PlayerState state) {
        List<Pattern> simplePatterns = PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getDifficulty() == Difficulty.EASY)
                .filter(p -> isPatternCompatibleWithState(p, state))
                .toList();
    
        if (simplePatterns.isEmpty()) {
            return new Pattern("SimpleFiller", List.of(GeneType.EMPTY, GeneType.BLOCK_GROUND), Difficulty.EASY);
        }

        return simplePatterns.get(new Random().nextInt(simplePatterns.size()));
    }

    private boolean isPatternCompatibleWithState(Pattern pattern, PlayerState state) {
        GeneType firstGene = pattern.getGenes().getFirst();
        return switch (state) {
            case FLYING -> firstGene != GeneType.SPIKE_GROUND && firstGene != GeneType.BLOCK_GROUND;
            case BALL -> true;
            default -> firstGene != GeneType.SPIKE_AIR;
        };
    }

    private void mutate(LevelChromosome level) {
        var result = playtesterForMutation.run(level, PlayerPersona.NORMAL);
        Map<Integer, Integer> heatmap = result.deathHeatmap;
        if (heatmap == null) {
            heatmap = new HashMap<>();
            System.out.println("Warning: deathHeatmap was null, using empty heatmap for mutation");
        }

        List<Pattern> patterns = level.getPatterns();
        for (int i = 0; i < patterns.size(); i++) {
            boolean shouldMutate = false;
            int patternStartGene = level.getGeneIndexForPattern(i);
            int patternEndGene = patternStartGene + patterns.get(i).getLength();

            for (int geneIdx = patternStartGene; geneIdx < patternEndGene; geneIdx++) {
                if (heatmap.getOrDefault(geneIdx, 0) > 0) {
                    shouldMutate = true;
                    break;
                }
            }
            if (shouldMutate || Math.random() < this.mutationRate) {
                PlayerState stateBeforePattern = getPlayerStateBeforePattern(patterns, i);
                Pattern currentPattern = patterns.get(i);
                Pattern easierPattern = PatternLibrary.findEasierAlternativeForState(currentPattern, stateBeforePattern);
                patterns.set(i, easierPattern);
            }
        }
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
        List<Pattern> longerParent = parent1Patterns.size() >= parent2Patterns.size() ? parent1Patterns : parent2Patterns;
        List<Pattern> shorterParent = parent1Patterns.size() < parent2Patterns.size() ? parent1Patterns : parent2Patterns;

        int crossoverPoint = (int) (Math.random() * shorterParent.size());
        List<Pattern> offspringPatterns = new ArrayList<>();
        offspringPatterns.addAll(longerParent.subList(0, crossoverPoint));
        offspringPatterns.addAll(shorterParent.subList(crossoverPoint, shorterParent.size()));

        if (crossoverPoint < longerParent.size()) {
            offspringPatterns.addAll(longerParent.subList(Math.min(crossoverPoint + shorterParent.size() - crossoverPoint, longerParent.size()), longerParent.size()));
        }
        return new LevelChromosome(offspringPatterns);
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
    private PlayerState getPlayerStateBeforePattern(List<Pattern> patterns, int index) {
        PlayerState state = PlayerState.NORMAL;
        for (int i = 0; i < index; i++) {
            state = getPlayerStateAfterPattern(patterns.get(i), state);
        }
        return state;
    }
    private int generateRandomPopulationSize() {
        return 150 + parameterRandom.nextInt(351);
    }

    private double generateRandomMutationRate() {
        return 0.1 + (parameterRandom.nextDouble() * 0.3);
    }

    private int generateRandomTournamentSize() {
        return 3 + parameterRandom.nextInt(6);
    }

    private int generateRandomMaxGenerations() {
        return 200 + parameterRandom.nextInt(601);
    }

}