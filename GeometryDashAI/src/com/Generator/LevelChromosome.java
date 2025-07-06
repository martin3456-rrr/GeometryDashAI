package com.Generator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class LevelChromosome {
    private final List<GeneType> genes;
    private double fitness = 0.0;

    public LevelChromosome(int length) {
        this.genes = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            this.genes.add(GeneType.values()[(int) (Math.random() * GeneType.values().length)]);
        }
    }

    public LevelChromosome(List<GeneType> genes) {
        this.genes = new ArrayList<>(genes);
    }

    public List<GeneType> getGenes() {
        return Collections.unmodifiableList(genes);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getLength() {
        return genes.size();
    }
}