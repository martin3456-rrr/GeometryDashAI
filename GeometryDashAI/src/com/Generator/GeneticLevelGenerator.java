package com.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class GeneticLevelGenerator {
    
    private final int populationSize = 100;
    private final int levelLength = 200;
    private final double mutationRate = 0.01;
    private final int tournamentSize = 5;

    private final int maxGenerations = 1000;
    private final double convergenceThreshold = 0.001;
    
    private List<LevelChromosome> population;
    private final FitnessCalculator fitnessCalculator = new FitnessCalculator();

    public GeneticLevelGenerator() {
        this.population = initializePopulation();
    }

    public LevelChromosome generateBestLevel() {
        double bestFitness = Double.MIN_VALUE;
        int generationsWithoutImprovement = 0;
        
        for (int generation = 0; generation < maxGenerations; generation++) {
            System.out.println("Generacja: " + (generation + 1));
            population = evolvePopulation();

            LevelChromosome currentBest = population.stream()
                .max(Comparator.comparing(LevelChromosome::getFitness))
                .orElse(null);
            
            if (currentBest != null) {
                double currentFitness = currentBest.getFitness();

                if (currentFitness > bestFitness + convergenceThreshold) {
                    bestFitness = currentFitness;
                    generationsWithoutImprovement = 0;
                    System.out.println("Nowy najlepszy fitness: " + bestFitness);
                } else {
                    generationsWithoutImprovement++;
                }

                if (currentFitness >= 0.95) {
                    System.out.println("Osiągnięto zadowalający poziom fitness: " + currentFitness);
                    break;
                }
            }
        }
        
        return population.stream().max(Comparator.comparing(LevelChromosome::getFitness)).orElse(null);
    }

    private List<LevelChromosome> initializePopulation() {
        List<LevelChromosome> newPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            newPopulation.add(new LevelChromosome(levelLength));
        }
        return newPopulation;
    }

    private List<LevelChromosome> evolvePopulation() {
        for (LevelChromosome level : population) {
            fitnessCalculator.calculateFitness(level);
        }

        List<LevelChromosome> newPopulation = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            LevelChromosome parent1 = tournamentSelection();
            LevelChromosome parent2 = tournamentSelection();
            LevelChromosome offspring = crossover(parent1, parent2);
            mutate(offspring);
            newPopulation.add(offspring);
        }

        return newPopulation;
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
        int crossoverPoint = (int) (Math.random() * parent1.getLength());
        List<GeneType> offspringGenes = new ArrayList<>();

        List<GeneType> parent1Genes = new ArrayList<>(parent1.getGenes().subList(0, crossoverPoint));
        List<GeneType> parent2Genes = new ArrayList<>(parent2.getGenes().subList(crossoverPoint, parent2.getLength()));
        
        offspringGenes.addAll(parent1Genes);
        offspringGenes.addAll(parent2Genes);
        
        return new LevelChromosome(offspringGenes);
    }

    private void mutate(LevelChromosome level) {
        List<GeneType> genes = level.getGenes();

        if (!(genes instanceof ArrayList)) {
            genes = new ArrayList<>(genes);
        }
        
        for (int i = 0; i < genes.size(); i++) {
            if (Math.random() < mutationRate) {
                GeneType[] allGeneTypes = GeneType.values();
                GeneType randomGene = allGeneTypes[(int) (Math.random() * allGeneTypes.length)];
                genes.set(i, randomGene);
            }
        }
    }
}