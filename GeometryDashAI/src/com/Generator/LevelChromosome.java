package com.Generator;

import java.util.List;
import java.util.ArrayList;

public class LevelChromosome {
    private final List<Pattern> patterns;
    private double fitness = 0.0;

    public LevelChromosome(List<Pattern> patterns) {
        this.patterns = new ArrayList<>(patterns);
    }
    public List<GeneType> getGenes() {
        return patterns.stream()
                .flatMap(pattern -> pattern.getGenes().stream()).toList();
    }
    public List<Pattern> getPatterns() {
        return this.patterns;
    }
    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    public int getGeneIndexForPattern(int patternIndex) {
        if (patternIndex < 0 || patternIndex >= patterns.size()) {
            throw new IndexOutOfBoundsException("Pattern index out of bounds");
        }

        int geneIndex = 0;
        for (int i = 0; i < patternIndex; i++) {
            geneIndex += patterns.get(i).getLength();
        }
        return geneIndex;
    }
}