package com.Generator;

import com.util.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//klasy pomocnicze
class PlayerPath {
    public final List<Vector2> points;
    public final boolean isOptimal;

    public PlayerPath(List<Vector2> points, boolean isOptimal) {
        this.points = points;
        this.isOptimal = isOptimal;
    }
}
class DifficultyProfile {
    public final List<Double> difficultyPerSegment;
    public final double averageDifficulty;

    public DifficultyProfile(List<Double> segments, double avg) {
        this.difficultyPerSegment = segments;
        this.averageDifficulty = avg;
    }
}
class TrajectoryAnalyzer {
    public List<PlayerPath> findAllViablePaths(LevelChromosome level) {
        List<Vector2> pathPoints = new ArrayList<>();
        float currentX = 0;
        float currentY = 0;
        for (GeneType gene : level.getGenes()) {
            if (gene == GeneType.SPIKE_GROUND) currentY = 50;
            else if (gene == GeneType.BLOCK_GROUND) currentY = 0;
            pathPoints.add(new Vector2(currentX, currentY));
            currentX += 42;
        }
        return List.of(new PlayerPath(pathPoints, true));
    }
}

class DifficultyAnalyzer {
    public DifficultyProfile analyzeDifficultyCurve(LevelChromosome level, Map<PlayerPersona, AIPlaytester.PlaytestResult> results) {
        List<Double> segments = new ArrayList<>();
        level.getPatterns().forEach(p -> segments.add((double) p.getDifficulty().ordinal()));
        double avg = segments.stream().mapToDouble(d -> d).average().orElse(0.0);
        return new DifficultyProfile(segments, avg);
    }
}

class ComprehensivePlaytestResult extends AIPlaytester.PlaytestResult {
    public final Map<PlayerPersona, AIPlaytester.PlaytestResult> detailedResults;
    public final List<PlayerPath> playerPaths;
    public final DifficultyProfile difficultyProfile;

    public ComprehensivePlaytestResult(Map<PlayerPersona, AIPlaytester.PlaytestResult> results, List<PlayerPath> paths, DifficultyProfile profile) {
        super(
                results.values().stream().mapToInt(r -> r.deaths).sum(),
                results.values().stream().mapToInt(r -> r.impossibleSections).sum(),
                0, new HashMap<>()
        );
        this.detailedResults = results;
        this.playerPaths = paths;
        this.difficultyProfile = profile;
    }
}

public class AdvancedPlaytester {
    private final List<PlayerPersona> testPersonas = List.of(PlayerPersona.CAUTIOUS, PlayerPersona.NORMAL, PlayerPersona.SPEEDRUNNER);
    private final TrajectoryAnalyzer trajectoryAnalyzer = new TrajectoryAnalyzer();
    private final DifficultyAnalyzer difficultyAnalyzer = new DifficultyAnalyzer();
    private final AIPlaytester basicPlaytester = new AIPlaytester();

    public ComprehensivePlaytestResult comprehensiveTest(LevelChromosome level) {
        Map<PlayerPersona, AIPlaytester.PlaytestResult> results = new HashMap<>();
        for (PlayerPersona persona : testPersonas) {
            results.put(persona, basicPlaytester.run(level, persona));
        }

        List<PlayerPath> possiblePaths = trajectoryAnalyzer.findAllViablePaths(level);

        DifficultyProfile profile = difficultyAnalyzer.analyzeDifficultyCurve(level, results);

        return new ComprehensivePlaytestResult(results, possiblePaths, profile);
    }
}