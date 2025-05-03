package com.Component;

import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.jade.Camera;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.manager.Difficulty;
import com.util.Constants;
import com.util.Vector2;

import java.util.Random;

enum ObstacleCategory {
    SPIKE, BLOCK, PORTAL, BIG_BLOCK, HALF_BLOCK, NONE
}

enum LevelTheme {
    ERA1_BASICS, // Stereo Madness, Back on Track, Polargeist
    ERA2_STRUCTURES, // Dry Out, Base After Base, Can't Let Go, Jumper
    ERA3_PRECISION, // Time Machine, Cycles, xStep, Clutterfunk
    ERA4_CHAOS  // Electroman, Electrodynamix, Hexagon Force, Blast Processing
}

public class LevelGenerator extends Component {
    private Difficulty currentDifficulty;
    private Spritesheet groundSpritesSheet;
    private Spritesheet spikesSheet;
    private Spritesheet bigSpritesSheet;
    private Spritesheet smallBlocksSheet;
    private Spritesheet portalSheet;

    private float timeSinceLastSpawn = 0;
    private float nextSpawnTime = 2.0f;
    private Random random = new Random();

    private final float OBSTACLE_SPAWN_OFFSET_X = Constants.SCREEN_WIDTH + 100;
    private float currentMinHorizontalSpacing = Constants.TILE_WIDTH * 0.5f;
    private float lastObstacleX = -1.0f;
    private float lastObstacleWidth = 0;
    private ObstacleCategory lastObstacleCategory = ObstacleCategory.NONE;
    private int consecutiveObstacleCount = 0;

    private LevelTheme currentTheme = LevelTheme.ERA1_BASICS;
    private final float ERA1_DISTANCE = 3500;
    private final float ERA2_DISTANCE = 4500;
    private final float ERA3_DISTANCE = 5500;
    private final float ERA4_DISTANCE = 6500;
    private float nextThemeChangeThreshold = ERA1_DISTANCE;

    private int obstaclesSpawnedCount = 0;


    public LevelGenerator(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.groundSpritesSheet = AssertPool.getSpritesheet("assets/groundSprites.png");
        this.spikesSheet = AssertPool.getSpritesheet("assets/spikes.png");
        this.bigSpritesSheet = AssertPool.getSpritesheet("assets/bigSprites.png");
        this.smallBlocksSheet = AssertPool.getSpritesheet("assets/smallBlocks.png");
        this.portalSheet = AssertPool.getSpritesheet("assets/portal.png");

        this.lastObstacleX = 50;

        calculateNextSpawnTime(true);
        this.nextSpawnTime += 1.0f;
    }

    @Override
    public void update(double dt) {
        if (groundSpritesSheet == null) { return; }

        Camera camera = Window.getWindow().getCurrentScene().camera;
        if (camera == null || camera.position == null) { return; }

        if (camera.position.x >= nextThemeChangeThreshold) {
            changeTheme(camera.position.x);
        }

        timeSinceLastSpawn += dt;
        if (timeSinceLastSpawn >= nextSpawnTime) {
            spawnObstacle();
        }
    }

    private void changeTheme(float currentCameraX) {
        LevelTheme nextTheme = currentTheme;

        switch (currentTheme) {
            case ERA1_BASICS:
                if (currentCameraX >= ERA1_DISTANCE) {
                    nextTheme = LevelTheme.ERA2_STRUCTURES;
                    nextThemeChangeThreshold = ERA1_DISTANCE + ERA2_DISTANCE;
                }
                break;
            case ERA2_STRUCTURES:
                if (currentCameraX >= (ERA1_DISTANCE + ERA2_DISTANCE)) {
                    nextTheme = LevelTheme.ERA3_PRECISION;
                    nextThemeChangeThreshold = ERA1_DISTANCE + ERA2_DISTANCE + ERA3_DISTANCE;
                }
                break;
            case ERA3_PRECISION:
                if (currentCameraX >= (ERA1_DISTANCE + ERA2_DISTANCE + ERA3_DISTANCE)) {
                    nextTheme = LevelTheme.ERA4_CHAOS;
                    nextThemeChangeThreshold = Float.MAX_VALUE;
                }
                break;
            case ERA4_CHAOS: break;
        }

        if (nextTheme != currentTheme) {
            currentTheme = nextTheme;
            calculateNextSpawnTime(true);
            lastObstacleCategory = ObstacleCategory.NONE;
            consecutiveObstacleCount = 0;
        }
    }

    private void calculateNextSpawnTime(boolean isFirstSpawnInTheme) {
        float baseTime = 0.8f;
        float randomRange = 0.1f;

        if (currentTheme == LevelTheme.ERA1_BASICS) {
            if (isFirstSpawnInTheme) { baseTime = 0.9f; }
            else if (lastObstacleCategory == ObstacleCategory.SPIKE && consecutiveObstacleCount < 2) { baseTime = 0.45f; randomRange = 0.05f;}
            else if (lastObstacleCategory == ObstacleCategory.BLOCK || lastObstacleCategory == ObstacleCategory.HALF_BLOCK) { baseTime = 0.6f; randomRange = 0.1f;}
            else { baseTime = 0.8f; }
        } else {
            switch (currentTheme) {
                case ERA2_STRUCTURES:
                    baseTime = isFirstSpawnInTheme ? 0.7f : (lastObstacleCategory == ObstacleCategory.BLOCK || lastObstacleCategory == ObstacleCategory.HALF_BLOCK ? 0.85f : 0.7f);
                    randomRange = 0.15f;
                    break;
                case ERA3_PRECISION:
                    baseTime = isFirstSpawnInTheme ? 0.6f : (lastObstacleCategory == ObstacleCategory.SPIKE ? 0.55f : 0.65f);
                    randomRange = 0.1f;
                    break;
                case ERA4_CHAOS:
                    baseTime = isFirstSpawnInTheme ? 0.5f : (lastObstacleCategory == ObstacleCategory.PORTAL ? 0.6f : 0.5f);
                    randomRange = 0.08f;
                    break;
                default: baseTime = 0.8f; break;
            }
        }

        float randomFactor = (random.nextFloat() * randomRange) - (randomRange / 2.0f);
        this.nextSpawnTime = Math.max(0.25f, baseTime + randomFactor);
    }


    private void spawnObstacle() {
        Camera camera = Window.getWindow().getCurrentScene().camera;
        if (camera == null || camera.position == null) { return; }

        float currentMinHorizontalSpacing;
        if (lastObstacleCategory == ObstacleCategory.SPIKE && consecutiveObstacleCount < 2) {
            currentMinHorizontalSpacing = Constants.TILE_WIDTH * 0.1f;
        } else {
            switch(currentTheme) {
                case ERA3_PRECISION: currentMinHorizontalSpacing = Constants.TILE_WIDTH * 0.3f; break;
                case ERA4_CHAOS: currentMinHorizontalSpacing = Constants.TILE_WIDTH * 0.2f; break;
                default: currentMinHorizontalSpacing = Constants.TILE_WIDTH * 0.5f; break;
            }
        }


        float potentialSpawnX = camera.position.x + OBSTACLE_SPAWN_OFFSET_X;
        float requiredSpawnX = (obstaclesSpawnedCount == 0) ? potentialSpawnX : lastObstacleX + lastObstacleWidth + currentMinHorizontalSpacing;
        float finalSpawnX = Math.max(potentialSpawnX, requiredSpawnX);

        ObstacleCreationResult result = createThemedObstacle(finalSpawnX);
        GameObject newObstacle = result.gameObject;

        if (newObstacle != null) {
            Window.getWindow().getCurrentScene().addGameObject(newObstacle);
            obstaclesSpawnedCount++;

            Bounds bounds = newObstacle.getComponent(Bounds.class);
            if (bounds != null) {
                this.lastObstacleX = finalSpawnX;
                this.lastObstacleWidth = bounds.getWidth();
            } else {
                this.lastObstacleX = finalSpawnX;
                this.lastObstacleWidth = Constants.TILE_WIDTH;
            }

            if (result.category == lastObstacleCategory && result.category != ObstacleCategory.NONE) {
                consecutiveObstacleCount++;
            } else {
                consecutiveObstacleCount = 1;
            }
            this.lastObstacleCategory = result.category;


            this.timeSinceLastSpawn = 0;
            calculateNextSpawnTime(false);

        }
    }

    private static class ObstacleCreationResult { GameObject gameObject; ObstacleCategory category; ObstacleCreationResult(GameObject go, ObstacleCategory cat) { this.gameObject = go; this.category = cat; } }

    private ObstacleCreationResult createThemedObstacle(float x) {
        float spawnY = Constants.GROUND_Y - Constants.TILE_HEIGHT;
        GameObject obj = null;
        Sprite chosenSprite = null;
        Bounds chosenBounds = null;
        ObstacleCategory currentCategory = ObstacleCategory.NONE;
        boolean allowFloatingBlocks = false;

        if (currentTheme == LevelTheme.ERA1_BASICS && obstaclesSpawnedCount < 5) {
            switch (obstaclesSpawnedCount) {
                case 0: currentCategory = ObstacleCategory.SPIKE; break;
                case 1: currentCategory = ObstacleCategory.SPIKE; break;
                case 2: currentCategory = ObstacleCategory.SPIKE; break;
                case 3: currentCategory = ObstacleCategory.BLOCK; break;
                case 4: currentCategory = ObstacleCategory.BLOCK; break;
                default: currentCategory = ObstacleCategory.NONE; break;
            }
        } else {
            int chance = random.nextInt(100);

            switch (currentTheme) {
                case ERA1_BASICS:
                    allowFloatingBlocks = false;
                    if (lastObstacleCategory == ObstacleCategory.SPIKE && consecutiveObstacleCount < 2 && random.nextInt(100) < 60) chance = 5;
                    else if (lastObstacleCategory == ObstacleCategory.BLOCK && random.nextInt(100) < 50) chance = 5;

                    if (chance < 45) { currentCategory = ObstacleCategory.SPIKE; }
                    else if (chance < 85) { currentCategory = ObstacleCategory.BLOCK; }
                    else if (chance < 95) { currentCategory = ObstacleCategory.HALF_BLOCK; }
                    else { currentCategory = ObstacleCategory.NONE; }
                    break;
                case ERA2_STRUCTURES:
                    allowFloatingBlocks = random.nextInt(100) < 5;
                    if (lastObstacleCategory == ObstacleCategory.BLOCK && consecutiveObstacleCount < 2 && random.nextInt(100) < 30) chance = 55;
                    else if (lastObstacleCategory == ObstacleCategory.SPIKE && random.nextInt(100) < 40) chance = 5;

                    if (chance < 40) { currentCategory = ObstacleCategory.SPIKE; }
                    else if (chance < 80) { currentCategory = ObstacleCategory.BLOCK; }
                    else if (chance < 90) { currentCategory = ObstacleCategory.HALF_BLOCK; }
                    else if (chance < 95 && currentDifficulty == Difficulty.HARD) { currentCategory = ObstacleCategory.BIG_BLOCK; }
                    else { currentCategory = ObstacleCategory.NONE; }
                    break;
                case ERA3_PRECISION:
                    allowFloatingBlocks = random.nextInt(100) < 15;
                    if (lastObstacleCategory == ObstacleCategory.HALF_BLOCK ) {
                        if (random.nextInt(100) < 60) {
                            currentCategory = ObstacleCategory.SPIKE;
                            chance = -1;
                        }
                    }

                    if (chance != -1) {
                        if (lastObstacleCategory == ObstacleCategory.SPIKE && consecutiveObstacleCount < 2 && random.nextInt(100) < 45) chance = 5;
                        else if (lastObstacleCategory == ObstacleCategory.BLOCK && random.nextInt(100) < 35) chance = 5;

                        if (chance < 55) { currentCategory = ObstacleCategory.SPIKE; }
                        else if (chance < 75) { currentCategory = ObstacleCategory.BLOCK; } // Mniej zwykłych bloków
                        else if (chance < 88) { currentCategory = ObstacleCategory.HALF_BLOCK; } // Więcej półbloków (mogą latać)
                        else if (chance < 95) { currentCategory = ObstacleCategory.PORTAL; }
                        else if (currentDifficulty == Difficulty.HARD) { currentCategory = ObstacleCategory.BIG_BLOCK; }
                        else { currentCategory = ObstacleCategory.NONE; }
                    }
                    break;
                case ERA4_CHAOS:
                    allowFloatingBlocks = random.nextInt(100) < 10;
                    if (lastObstacleCategory == ObstacleCategory.SPIKE && consecutiveObstacleCount < 2 && random.nextInt(100) < 55) chance = 5;
                    else if (lastObstacleCategory == ObstacleCategory.PORTAL && random.nextInt(100) < 60) chance = 5;
                    else if (lastObstacleCategory == ObstacleCategory.BLOCK && random.nextInt(100) < 30) chance = 5;

                    if (chance < 65) { currentCategory = ObstacleCategory.SPIKE; }
                    else if (chance < 75) { currentCategory = ObstacleCategory.BLOCK; }
                    else if (chance < 80) { currentCategory = ObstacleCategory.HALF_BLOCK; }
                    else if (chance < 95) { currentCategory = ObstacleCategory.PORTAL; }
                    else if (currentDifficulty == Difficulty.HARD) { currentCategory = ObstacleCategory.BIG_BLOCK; }
                    else { currentCategory = ObstacleCategory.NONE; }
                    break;
            }
        }
        switch (currentCategory) {
            case SPIKE:
                if (spikesSheet != null && !spikesSheet.sprite.isEmpty()) {
                    chosenSprite = (Sprite) spikesSheet.sprite.get(random.nextInt(spikesSheet.sprite.size())).copy();
                    chosenBounds = new TriangleBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                    spawnY = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                } else { currentCategory = ObstacleCategory.NONE; }
                break;
            case BLOCK:
                Spritesheet blockSheet = (groundSpritesSheet != null && !groundSpritesSheet.sprite.isEmpty() && random.nextBoolean())
                        ? groundSpritesSheet : smallBlocksSheet;
                if (blockSheet != null && !blockSheet.sprite.isEmpty()) {
                    int spriteIndex = random.nextInt(blockSheet.sprite.size());
                    if (blockSheet == smallBlocksSheet && spriteIndex == 0 && blockSheet.sprite.size() > 1) spriteIndex = 1;
                    if (spriteIndex < blockSheet.sprite.size()) {
                        chosenSprite = (Sprite) blockSheet.sprite.get(spriteIndex).copy();
                        chosenBounds = new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                        spawnY = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    } else { currentCategory = ObstacleCategory.NONE; }
                } else { currentCategory = ObstacleCategory.NONE; }
                break;
            case HALF_BLOCK:
                if (smallBlocksSheet != null && !smallBlocksSheet.sprite.isEmpty()) {
                    chosenSprite = (Sprite) smallBlocksSheet.sprite.get(0).copy();
                    chosenBounds = new BoxBounds(Constants.TILE_WIDTH, 16);
                    ((BoxBounds) chosenBounds).yBuffer = Constants.TILE_HEIGHT - 16;
                    spawnY = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                } else { currentCategory = ObstacleCategory.NONE; }
                break;
            case BIG_BLOCK:
                if (bigSpritesSheet != null && !bigSpritesSheet.sprite.isEmpty()) {
                    chosenSprite = (Sprite) bigSpritesSheet.sprite.get(random.nextInt(bigSpritesSheet.sprite.size())).copy();
                    chosenBounds = new BoxBounds(Constants.TILE_WIDTH * 2, 56);
                    spawnY = Constants.GROUND_Y - 56;
                } else { currentCategory = ObstacleCategory.NONE; }
                break;
            case PORTAL:
                if (portalSheet != null && !portalSheet.sprite.isEmpty()) {
                    int spriteIndex = random.nextInt(portalSheet.sprite.size());
                    chosenSprite = (Sprite) portalSheet.sprite.get(spriteIndex).copy();
                    chosenBounds = new BoxBounds(44, 85, true);
                    spawnY = Constants.GROUND_Y - 85;
                } else { currentCategory = ObstacleCategory.NONE; }
                break;
            case NONE: default: break;
        }

        if (currentCategory == ObstacleCategory.HALF_BLOCK && allowFloatingBlocks) {
            if (random.nextInt(100) < 50) {
                int randomHeightOffset = (random.nextInt(2) + 1) * Constants.TILE_HEIGHT;
                spawnY -= randomHeightOffset;
                float minY = Constants.GROUND_Y - Constants.TILE_HEIGHT * 4;
                spawnY = Math.max(spawnY, minY);
            }
        }

        if (chosenSprite != null && chosenBounds != null) {
            obj = new GameObject("GeneratedObstacle", new Transform(new Vector2(x, spawnY)), 1);
            obj.addComponent(chosenSprite); obj.addComponent(chosenBounds);
            obj.isUI = false; obj.setNonserializable();
            return new ObstacleCreationResult(obj, currentCategory);
        }

        return new ObstacleCreationResult(null, ObstacleCategory.NONE);
    }


    @Override
    public Component copy() { return null; }
}