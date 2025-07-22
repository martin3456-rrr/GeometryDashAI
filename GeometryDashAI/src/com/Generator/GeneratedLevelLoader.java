package com.Generator;

import com.Component.*;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.jade.GameObject;
import com.util.Constants;
import com.util.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneratedLevelLoader {
    private final Random random = new Random();
    private int blockSpriteIndex = 0;
    private int spikeSpriteIndex = 0;

    public List<GameObject> translateChromosomeToGameObjects(LevelChromosome chromosome) {
        List<GameObject> gameObjects = new ArrayList<>();
        float currentX = 10.0f;

        for (GeneType gene : chromosome.getGenes()) {
            switch (gene) {
                case EMPTY:
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_GROUND:
                    int choice = random.nextInt(10);
                    if (choice < 1) {
                        gameObjects.add(createBigBlock(currentX, Constants.GROUND_Y - 56));
                        currentX += Constants.TILE_WIDTH * 2;
                    } else if (choice < 3) {
                        gameObjects.add(createSmallBlock(currentX, Constants.GROUND_Y - 16));
                        currentX += Constants.TILE_WIDTH;
                    } else {
                        gameObjects.add(createBlock(currentX, Constants.GROUND_Y - Constants.TILE_HEIGHT));
                        currentX += Constants.TILE_WIDTH;
                    }
                    break;
                case SPIKE_GROUND:
                    gameObjects.add(createSpike(currentX, Constants.GROUND_Y - Constants.TILE_HEIGHT));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case THREE_SPIKES_GROUND:
                    float y_spikes = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    for (int i = 0; i < 3; i++) {
                        gameObjects.add(createSpike(currentX, y_spikes));
                        currentX += Constants.TILE_WIDTH;
                    }
                    break;
                case JUMP_PAD:
                    gameObjects.add(createJumpPad(currentX, Constants.GROUND_Y - Constants.TILE_HEIGHT));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_FLOATING:
                    gameObjects.add(createBlock(currentX, Constants.GROUND_Y - (Constants.TILE_HEIGHT * 3)));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_AIR:
                    gameObjects.add(createBlock(currentX, Constants.GROUND_Y - (Constants.TILE_HEIGHT * 4)));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case SPIKE_AIR:
                    gameObjects.add(createSpike(currentX, Constants.GROUND_Y - (Constants.TILE_HEIGHT * 3)));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_SMALL:
                    gameObjects.add(createSmallBlock(currentX, Constants.GROUND_Y - 16));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_BIG:
                    gameObjects.add(createBigBlock(currentX, Constants.GROUND_Y - 56));
                    currentX += Constants.TILE_WIDTH * 2;
                    break;
                case PORTAL_GRAVITY_UP:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.GRAVITY_UP));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case PORTAL_GRAVITY_DOWN:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.GRAVITY_DOWN));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case PORTAL_SHIP:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.SHIP));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case PORTAL_BALL:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.BALL));
                    currentX += Constants.TILE_WIDTH;
                    break;
                case PORTAL_NORMAL:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.CUBE));
                    currentX += Constants.TILE_WIDTH;
                    break;
            }
        }
        return gameObjects;
    }

    private GameObject createBlock(float x, float y) {
        GameObject block = new GameObject("Block", new Transform(new Vector2(x, y)), 1);
        Spritesheet groundSprites = AssertPool.getSpritesheet("assets/groundSprites.png");
        if (groundSprites != null && !groundSprites.sprite.isEmpty()) {
            int index = blockSpriteIndex % groundSprites.sprite.size();
            block.addComponent(groundSprites.sprite.get(index).copy());
            blockSpriteIndex++;
        }
        block.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        block.setNonserializable();
        return block;
    }

    private GameObject createSpike(float x, float y) {
        GameObject spike = new GameObject("Spike", new Transform(new Vector2(x, y)), 1);
        Spritesheet spikeSprites = AssertPool.getSpritesheet("assets/spikes.png");
        if (spikeSprites != null && !spikeSprites.sprite.isEmpty()) {
            int index = spikeSpriteIndex % spikeSprites.sprite.size();
            spike.addComponent(spikeSprites.sprite.get(index).copy());
            spikeSpriteIndex++;
        }
        spike.addComponent(new TriangleBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        spike.setNonserializable();
        return spike;
    }

    private GameObject createSmallBlock(float x, float y) {
        GameObject block = new GameObject("SmallBlock", new Transform(new Vector2(x, y)), 1);
        Spritesheet smallSprites = AssertPool.getSpritesheet("assets/smallBlocks.png");
        if (smallSprites != null && !smallSprites.sprite.isEmpty()) {
            block.addComponent(smallSprites.sprite.get(random.nextInt(smallSprites.sprite.size())).copy());
        }
        BoxBounds bounds = new BoxBounds(Constants.TILE_WIDTH, 16);
        bounds.yBuffer = 42 - 16;
        block.addComponent(bounds);
        block.setNonserializable();
        return block;
    }

    private GameObject createBigBlock(float x, float y) {
        GameObject block = new GameObject("BigBlock", new Transform(new Vector2(x, y)), 1);
        Spritesheet bigSprites = AssertPool.getSpritesheet("assets/bigSprites.png");
        if (bigSprites != null && !bigSprites.sprite.isEmpty()) {
            block.addComponent(bigSprites.sprite.get(random.nextInt(bigSprites.sprite.size())).copy());
        }
        block.addComponent(new BoxBounds(Constants.TILE_WIDTH * 2, 56));
        block.setNonserializable();
        return block;
    }

    private GameObject createJumpPad(float x, float y) {
        GameObject pad = new GameObject("JumpPad", new Transform(new Vector2(x, y)), 2);
        pad.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT / 2.0f));
        pad.addComponent(new JumpPad(JumpPad.PadType.YELLOW_PAD));
        pad.setNonserializable();
        return pad;
    }

    private GameObject createPortal(float x, float y, StateChangePortal.PortalType type) {
        GameObject portal = new GameObject("Portal", new Transform(new Vector2(x, y)), 2);
        portal.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT * 2, true));
        portal.addComponent(new StateChangePortal(type));
        portal.addComponent(AssertPool.getSpritesheet("assets/portal.png").sprite.getFirst().copy());
        portal.setNonserializable();
        return portal;
    }
}