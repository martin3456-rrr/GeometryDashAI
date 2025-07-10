package com.Generator;
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

    public static Map<String, List<String>> getOriginalLevels() {
        return Map.of(
                "stereo_madness", List.of(
                        "SimpleJump",
                        "BlockHop",
                        "SimpleJump",
                        "ThreeSpikes",
                        "BlockHop",
                        "StairwayUp",
                        "PlatformJump",
                        "JumpPadAction",
                        "PortalToShip",
                        "ShipTunnel",
                        "TightFly",
                        "ShipObstacleDive",
                        "PortalToNormal",
                        "BlockHop",
                        "DoubleSpikeJump",
                        "ThreeSpikes",
                        "StairwayUp",
                        "PortalToBall",
                        "BallGravitySwitch",
                        "BallPlatformSwitch",
                        "PortalToNormal",
                        "DoubleSpikeJump"
                )
        );
    }
}