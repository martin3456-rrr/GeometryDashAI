package com.Generator;

import com.Component.PlayerState;
import com.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIPlaytester {
    private double botY;
    private double botVelocityY;
    private PlayerState botState;
    private float gravityMultiplier;
    private Map<Integer, Integer> deathHeatmap;

    private int deaths = 0;
    private int jumps = 0;
    private int impossibleSections = 0;
    private int boringStretches = 0;

    public static class PlaytestResult {
        public final int deaths;
        public final int impossibleSections;
        public final int boringStretches;
        public final boolean isPlayable;
        public final Map<Integer, Integer> deathHeatmap;

        public PlaytestResult(int deaths, int impossible, int boring,Map<Integer, Integer> heatmap) {
            this.deaths = deaths;
            this.deathHeatmap = heatmap;
            this.impossibleSections = impossible;
            this.boringStretches = boring;
            this.isPlayable = deaths == 0 && impossible == 0;
        }
    }

    public PlaytestResult run(LevelChromosome level,PlayerPersona persona) {
        deathHeatmap = new HashMap<>();
        List<GeneType> genes = level.getGenes();
        for (int i = 0; i < genes.size(); i++) {
            GeneType currentGene = genes.get(i);
            List<GeneType> futureGenes = genes.subList(i, Math.min(i + 5, genes.size()));
            makeDecision(futureGenes, persona);
            if (checkDeath(currentGene)) {
                deaths++;
                int deathCoord = i;
                deathHeatmap.put(deathCoord, deathHeatmap.getOrDefault(deathCoord, 0) + 1);
                resetBotState();
            }
            updateBotPhysics();
            return new PlaytestResult(deaths, impossibleSections, boringStretches, deathHeatmap);
        }

        return new PlaytestResult(deaths, impossibleSections, boringStretches, deathHeatmap);
    }
    private void resetBotState() {
        this.botY = 0;
        this.botVelocityY = 0;
        this.botState = PlayerState.NORMAL;
        this.gravityMultiplier = 1.0f;
    }

    private void updateBotPhysics() {
        if (botState == PlayerState.FLYING) {
            botVelocityY -= 0.1;
        } else if (botY > 0 || botVelocityY != 0) {
            botVelocityY -= Constants.GRAVITY / 3000;
        }

        botY += botVelocityY;

        if (botY < 0) {
            botY = 0;
            botVelocityY = 0;
        }
    }

    private void makeDecision(List<GeneType> futureGenes, PlayerPersona persona) {
        if (botState != PlayerState.NORMAL || botY > 0.1) return;

        int jumpLookahead = switch (persona) {
            case CAUTIOUS -> 3;
            case NORMAL -> 2;
            case SPEEDRUNNER -> 1;
        };
        for (int i = 1; i < Math.min(jumpLookahead + 1, futureGenes.size()); i++) {
            GeneType gene = futureGenes.get(i);
            if (gene == GeneType.SPIKE_GROUND || gene == GeneType.BLOCK_GROUND) {
                if(isJumpTrajectorySafe(i, futureGenes)) {
                    botVelocityY = Constants.JUMP_FORCE / 60;
                    jumps++;
                    return;
                }
            }
        }
    }

    private boolean checkDeath(GeneType current) {
        if (current == GeneType.SPIKE_GROUND && botY <= 0.1 && gravityMultiplier == 1.0f) {
            return true;
        }
        if (current == GeneType.SPIKE_AIR && botY > 0 && gravityMultiplier == -1.0f) {
            return true;
        }
        return botState == PlayerState.FLYING && botY > 3.0;
    }
    private boolean isJumpTrajectorySafe(int startIndex, List<GeneType> genes) {
        double simBotY = 0;
        double simBotVelocityY = Constants.JUMP_FORCE / 60.0;

        for (int i = 0; i < genes.size(); i++) {
            simBotY += simBotVelocityY;
            simBotVelocityY -= Constants.GRAVITY / 3000.0;
            if (simBotY < 0) simBotY = 0;

            int worldIndex = startIndex + i;
            if (worldIndex < genes.size()) {
                GeneType gene = genes.get(worldIndex);
                if (gene == GeneType.SPIKE_GROUND && simBotY <= 0.1) {
                    return false;
                }
                if (gene == GeneType.BLOCK_GROUND && simBotY <= 0.1) {
                    return true;
                }
            }
        }
        return false;
    }
}