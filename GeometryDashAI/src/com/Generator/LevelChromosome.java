package com.Generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class LevelChromosome {
    private final List<Pattern> patterns;
    private double fitness = 0.0;

    public LevelChromosome(List<Pattern> patterns) {
        this.patterns = new ArrayList<>(patterns);
    }
    public List<GeneType> getGenes() {
        List<GeneType> allGenes = patterns.stream()
                .flatMap(pattern -> pattern.getGenes().stream())
                .collect(Collectors.toList());
        return Collections.unmodifiableList(allGenes);
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
}