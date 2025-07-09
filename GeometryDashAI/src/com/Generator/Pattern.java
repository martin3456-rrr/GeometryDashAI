package com.Generator;

import com.manager.Difficulty;
import java.util.List;

public class Pattern {
    private final String name;
    private final List<GeneType> genes;
    private final Difficulty difficulty;

    public Pattern(String name, List<GeneType> genes, Difficulty difficulty) {
        this.name = name;
        this.genes = genes;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public List<GeneType> getGenes() {
        return genes;
    }

    public int getLength() {
        return genes.size();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}