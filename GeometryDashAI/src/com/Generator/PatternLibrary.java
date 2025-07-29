package com.Generator;
import com.Component.PlayerState;
import com.manager.Difficulty;
import java.util.*;
import java.util.stream.Collectors;

public class PatternLibrary {
    public static final List<Pattern> PATTERNS = new ArrayList<>();
    private static final Map<String, Pattern> PATTERN_MAP;
    private static final Random random = new Random();
    static {
        PATTERNS.add(new Pattern("SimpleJump", List.of(GeneType.EMPTY, GeneType.SPIKE_GROUND, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("BlockHop", List.of(GeneType.BLOCK_GROUND, GeneType.EMPTY, GeneType.BLOCK_GROUND), Difficulty.EASY));
        PATTERNS.add(new Pattern("StairwayUp", List.of(GeneType.BLOCK_GROUND, GeneType.EMPTY, GeneType.BLOCK_FLOATING, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("PlatformJump", List.of(GeneType.BLOCK_FLOATING, GeneType.EMPTY, GeneType.EMPTY, GeneType.EMPTY, GeneType.BLOCK_FLOATING), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("ThreeSpikes", List.of(GeneType.THREE_SPIKES_GROUND, GeneType.EMPTY, GeneType.EMPTY), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("DoubleSpikeJump", List.of(GeneType.EMPTY, GeneType.SPIKE_GROUND, GeneType.SPIKE_GROUND, GeneType.EMPTY), Difficulty.HARD));
        PATTERNS.add(new Pattern("JumpPadAction", List.of(GeneType.EMPTY, GeneType.JUMP_PAD, GeneType.BLOCK_AIR, GeneType.EMPTY), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("StereoMadnessStart", List.of(GeneType.BLOCK_GROUND, GeneType.BLOCK_GROUND, GeneType.EMPTY, GeneType.SPIKE_GROUND), Difficulty.EASY));
        PATTERNS.add(new Pattern("StereoMadnessTripleBlock", List.of(GeneType.BLOCK_GROUND, GeneType.BLOCK_GROUND, GeneType.BLOCK_GROUND, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("ShipTunnel", List.of(GeneType.EMPTY, GeneType.BLOCK_FLOATING, GeneType.EMPTY, GeneType.BLOCK_FLOATING), Difficulty.EASY));
        PATTERNS.add(new Pattern("TightFly", List.of(GeneType.BLOCK_FLOATING, GeneType.EMPTY, GeneType.EMPTY, GeneType.BLOCK_FLOATING), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("ShipObstacleDive", List.of(GeneType.BLOCK_AIR, GeneType.EMPTY, GeneType.EMPTY, GeneType.BLOCK_GROUND), Difficulty.HARD));
        PATTERNS.add(new Pattern("SpikeFlyover", List.of(GeneType.SPIKE_GROUND, GeneType.SPIKE_GROUND, GeneType.EMPTY, GeneType.EMPTY), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("BallGravitySwitch", List.of(GeneType.BLOCK_GROUND, GeneType.EMPTY, GeneType.BLOCK_AIR), Difficulty.EASY));
        PATTERNS.add(new Pattern("BallZigZag", List.of(GeneType.BLOCK_GROUND, GeneType.SPIKE_AIR, GeneType.EMPTY, GeneType.BLOCK_AIR, GeneType.SPIKE_GROUND), Difficulty.HARD));
        PATTERNS.add(new Pattern("BallPlatformSwitch", List.of(GeneType.BLOCK_GROUND, GeneType.EMPTY, GeneType.EMPTY, GeneType.PORTAL_GRAVITY_DOWN, GeneType.BLOCK_AIR), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("PortalToShip", List.of(GeneType.PORTAL_SHIP), Difficulty.EASY));
        PATTERNS.add(new Pattern("PortalToBall", List.of(GeneType.PORTAL_BALL), Difficulty.EASY));
        PATTERNS.add(new Pattern("PortalToNormal", List.of(GeneType.PORTAL_NORMAL), Difficulty.EASY));
        PATTERNS.add(new Pattern("EnterShipEasy", List.of(GeneType.EMPTY, GeneType.EMPTY, GeneType.PORTAL_SHIP, GeneType.EMPTY, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("EnterBallFromGround", List.of(GeneType.BLOCK_GROUND, GeneType.PORTAL_BALL, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("xStepJump", List.of(GeneType.BLOCK_FLOATING, GeneType.SPIKE_AIR, GeneType.BLOCK_GROUND, GeneType.EMPTY), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("BigBlockHop", List.of(GeneType.BLOCK_BIG, GeneType.EMPTY, GeneType.EMPTY, GeneType.BLOCK_BIG), Difficulty.MEDIUM));
        PATTERNS.add(new Pattern("SmallStairs", List.of(GeneType.BLOCK_SMALL, GeneType.EMPTY, GeneType.BLOCK_GROUND), Difficulty.EASY));
        PATTERNS.add(new Pattern("BigWall", List.of(GeneType.EMPTY, GeneType.BLOCK_BIG), Difficulty.EASY));
        PATTERNS.add(new Pattern("SmallRunSpike", List.of(GeneType.BLOCK_SMALL, GeneType.BLOCK_SMALL, GeneType.SPIKE_GROUND, GeneType.EMPTY), Difficulty.HARD));
        PATTERNS.add(new Pattern("EmptySpace", List.of(GeneType.EMPTY, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("SingleBlock", List.of(GeneType.BLOCK_GROUND), Difficulty.EASY));
        PATTERNS.add(new Pattern("SafeRun", List.of(GeneType.EMPTY, GeneType.EMPTY, GeneType.EMPTY), Difficulty.EASY));
        PATTERNS.add(new Pattern("BasicSpike", List.of(GeneType.EMPTY, GeneType.SPIKE_GROUND), Difficulty.EASY));
        PATTERNS.add(new Pattern("BlockRun", List.of(GeneType.BLOCK_GROUND, GeneType.BLOCK_GROUND), Difficulty.EASY));
        PATTERN_MAP = PATTERNS.stream().collect(Collectors.toMap(Pattern::getName, p -> p));
    }

    public static Pattern getRandomPattern() {
        return PATTERNS.get(random.nextInt(PATTERNS.size()));
    }

    public static Pattern getPatternByName(String name) {
        return PATTERN_MAP.get(name);
    }

    public static List<Pattern> getPatternsFromNames(List<String> patternNames) {
        return patternNames.stream()
                .map(PatternLibrary::getPatternByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static Pattern findEasierAlternativeForState(Pattern originalPattern, PlayerState state) {
        Difficulty originalDifficulty = originalPattern.getDifficulty();
        if (originalDifficulty == Difficulty.EASY) return originalPattern;

        Difficulty targetDifficulty = (originalDifficulty == Difficulty.HARD) ? Difficulty.MEDIUM : Difficulty.EASY;

        List<GeneType> allowedStartingGenes = new ArrayList<>();
        if (state == PlayerState.FLYING) {
            allowedStartingGenes.add(GeneType.BLOCK_AIR);
            allowedStartingGenes.add(GeneType.EMPTY);
        } else {
            allowedStartingGenes.add(GeneType.BLOCK_GROUND);
            allowedStartingGenes.add(GeneType.SPIKE_GROUND);
            allowedStartingGenes.add(GeneType.EMPTY);
        }

        List<Pattern> alternatives = PATTERNS.stream()
                .filter(p -> p.getDifficulty() == targetDifficulty && allowedStartingGenes.contains(p.getGenes().getFirst()))
                .toList();

        if (!alternatives.isEmpty()) {
            return alternatives.get(random.nextInt(alternatives.size()));
        }

        return originalPattern;
    }
    public static boolean isPatternCompatibleWithState(Pattern pattern, PlayerState state) {
        if (pattern == null || pattern.getGenes().isEmpty()) return false;
        GeneType firstGene = pattern.getGenes().getFirst();

        return switch (state) {
            case FLYING -> firstGene != GeneType.SPIKE_GROUND && firstGene != GeneType.BLOCK_GROUND;
            case BALL -> true;
            default -> firstGene != GeneType.SPIKE_AIR;
        };
    }
    public static Pattern findAlternativeOfSameDifficulty(Pattern originalPattern, PlayerState state) {
        List<Pattern> alternatives = PATTERNS.stream()
                .filter(p -> p.getDifficulty() == originalPattern.getDifficulty())
                .filter(p -> !p.getName().equals(originalPattern.getName()))
                .filter(p -> isPatternCompatibleWithState(p, state))
                .toList();

        if (!alternatives.isEmpty()) {
            return alternatives.get(random.nextInt(alternatives.size()));
        }

        return originalPattern;
    }
    public static Pattern getRandomPatternCompatibleWithState(PlayerState state) {
        List<Pattern> compatiblePatterns = PATTERNS.stream()
                .filter(p -> isPatternCompatibleWithState(p, state))
                .toList();

        if (!compatiblePatterns.isEmpty()) {
            return compatiblePatterns.get(random.nextInt(compatiblePatterns.size()));
        }
        return getPatternByName("EmptySpace");
    }
    public static Map<String, List<String>> getOriginalLevels() {
        return Map.ofEntries(
                // Poziomy zostały wzbogacone o nowe, bardziej złożone wzorce
                Map.entry("Stereo Madness", List.of("StereoMadnessStart", "BigBlockHop", "StereoMadnessTripleBlock", "SimpleJump", "SmallRunSpike", "PlatformJump", "EnterShipEasy", "ShipTunnel", "PortalToNormal", "BlockHop")),
                Map.entry("Back On Track", List.of("BlockHop", "SimpleJump", "JumpPadAction", "SmallStairs", "JumpPadAction", "BigWall", "StairwayUp", "BigBlockHop")),
                Map.entry("Polargeist", List.of("SimpleJump", "StairwayUp", "SimpleJump", "JumpPadAction", "PlatformJump", "DoubleSpikeJump", "PortalToNormal", "BigWall")),
                Map.entry("Dry Out", List.of("BlockHop", "ThreeSpikes", "StairwayUp", "PlatformJump", "PortalToShip", "SpikeFlyover", "PortalToNormal", "SimpleJump")),
                Map.entry("Base After Base", List.of("StereoMadnessStart", "JumpPadAction", "SimpleJump", "DoubleSpikeJump", "PlatformJump", "ThreeSpikes", "JumpPadAction", "PortalToNormal")),
                Map.entry("Can't Let Go", List.of("ThreeSpikes", "DoubleSpikeJump", "BlockHop", "StairwayUp", "PortalToShip", "TightFly", "PortalToNormal", "DoubleSpikeJump")),
                Map.entry("Jumper", List.of("PlatformJump", "SimpleJump", "StairwayUp", "BlockHop", "ThreeSpikes", "DoubleSpikeJump", "PlatformJump", "EnterShipEasy", "ShipTunnel", "PortalToNormal")),
                Map.entry("Time Machine", List.of("ThreeSpikes", "SimpleJump", "ThreeSpikes", "SimpleJump", "EnterShipEasy", "TightFly", "ShipObstacleDive", "PortalToNormal", "DoubleSpikeJump")),
                Map.entry("Cycles", List.of("BlockHop", "StairwayUp", "PortalToBall", "BallGravitySwitch", "BallPlatformSwitch", "PortalToShip", "ShipObstacleDive", "PortalToNormal", "DoubleSpikeJump")),
                Map.entry("xStep", List.of("xStepJump", "JumpPadAction", "ThreeSpikes", "PortalToBall", "BallZigZag", "PortalToNormal", "PlatformJump", "DoubleSpikeJump")),
                Map.entry("Clutterfunk", List.of("DoubleSpikeJump", "ThreeSpikes", "PortalToShip", "TightFly", "ShipObstacleDive", "PortalToBall", "BallZigZag", "PortalToNormal", "xStepJump")),
                Map.entry("Theory of Everything", List.of("PlatformJump", "JumpPadAction", "PortalToShip", "SpikeFlyover", "PortalToBall", "BallPlatformSwitch", "PortalToNormal", "ThreeSpikes", "DoubleSpikeJump")),
                Map.entry("Electroman Adventures", List.of("JumpPadAction", "PlatformJump", "EnterBallFromGround", "BallGravitySwitch", "BallPlatformSwitch", "PortalToShip", "ShipObstacleDive", "TightFly", "PortalToNormal", "xStepJump")),
                Map.entry("Clubstep", List.of("DoubleSpikeJump", "xStepJump", "PortalToShip", "ShipObstacleDive", "TightFly", "ShipObstacleDive", "PortalToNormal", "DoubleSpikeJump", "ThreeSpikes", "DoubleSpikeJump")),
                Map.entry("Electrodynamix", List.of("DoubleSpikeJump", "ThreeSpikes", "JumpPadAction", "PortalToShip", "TightFly", "SpikeFlyover", "PortalToNormal", "xStepJump", "DoubleSpikeJump")),
                Map.entry("Hexagon Force", List.of("PlatformJump", "DoubleSpikeJump", "PortalToBall", "BallZigZag", "BallPlatformSwitch", "PortalToShip", "ShipObstacleDive", "PortalToNormal", "ThreeSpikes")),
                Map.entry("Blast Processing", List.of("StairwayUp", "BlockHop", "PortalToShip", "ShipTunnel", "SpikeFlyover", "TightFly", "PortalToNormal", "JumpPadAction", "PlatformJump")),
                Map.entry("Theory of Everything 2", List.of("xStepJump", "DoubleSpikeJump", "PortalToShip", "ShipObstacleDive", "ShipObstacleDive", "PortalToBall", "BallZigZag", "PortalToShip", "TightFly", "PortalToNormal", "DoubleSpikeJump")),
                Map.entry("Geometrical Dominator", List.of("BlockHop", "JumpPadAction", "ThreeSpikes", "PlatformJump", "EnterShipEasy", "ShipTunnel", "PortalToNormal", "StairwayUp", "DoubleSpikeJump")),
                Map.entry("Deadlocked", List.of("DoubleSpikeJump", "xStepJump", "PortalToShip", "ShipObstacleDive", "ShipObstacleDive", "PortalToBall", "BallZigZag", "PortalToShip", "TightFly", "PortalToNormal", "DoubleSpikeJump", "ThreeSpikes", "DoubleSpikeJump")),
                Map.entry("Fingerdash", List.of("JumpPadAction", "SimpleJump", "PortalToBall", "BallGravitySwitch", "PortalToShip", "SpikeFlyover", "PortalToNormal", "xStepJump", "DoubleSpikeJump", "ThreeSpikes")),
                Map.entry("Dash", List.of("StereoMadnessStart", "JumpPadAction", "PlatformJump", "PortalToShip", "TightFly", "PortalToBall", "BallPlatformSwitch", "PortalToNormal", "DoubleSpikeJump"))
        );
    }
}