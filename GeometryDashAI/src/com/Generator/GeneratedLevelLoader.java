package com.Generator;

import com.Component.BoxBounds;
import com.Component.JumpPad;
import com.Component.StateChangePortal;
import com.Component.TriangleBounds;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.jade.GameObject;
import com.util.Constants;
import com.util.Vector2;
import java.util.ArrayList;
import java.util.List;

public class GeneratedLevelLoader {
    public List<GameObject> translateChromosomeToGameObjects(LevelChromosome chromosome) {
        List<GameObject> gameObjects = new ArrayList<>();
        float currentX = 150.0f;

        for (GeneType gene : chromosome.getGenes()) {
            switch (gene) {
                case EMPTY:
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_GROUND:
                    float y_ground = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    GameObject block = createBlock(currentX, y_ground);
                    gameObjects.add(block);
                    currentX += Constants.TILE_WIDTH;
                    break;
                case SPIKE_GROUND:
                    float y_spike = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    GameObject spike = createSpike(currentX, y_spike);
                    gameObjects.add(spike);
                    currentX += Constants.TILE_WIDTH;
                    break;
                case THREE_SPIKES_GROUND:
                    float y_spikes = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    for (int i = 0; i < 3; i++) {
                        GameObject multiSpike = createSpike(currentX, y_spikes);
                        gameObjects.add(multiSpike);
                        currentX += Constants.TILE_WIDTH;
                    }
                    break;
                case JUMP_PAD:
                    float y_pad = Constants.GROUND_Y - Constants.TILE_HEIGHT;
                    GameObject pad = createJumpPad(currentX, y_pad);
                    gameObjects.add(pad);
                    currentX += Constants.TILE_WIDTH;
                    break;
                case BLOCK_FLOATING:
                    float y_floating = Constants.GROUND_Y - (Constants.TILE_HEIGHT * 3);
                    GameObject floatingBlock = createBlock(currentX, y_floating);
                    gameObjects.add(floatingBlock);
                    currentX += Constants.TILE_WIDTH;
                    break;
                case PORTAL_GRAVITY_UP:
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.GRAVITY_UP));
                    currentX += Constants.TILE_WIDTH; // Portale zajmują miejsce
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
                    gameObjects.add(createPortal(currentX, Constants.GROUND_Y - 100, StateChangePortal.PortalType.CUBE)); // CUBE to odpowiednik NORMAL
                    currentX += Constants.TILE_WIDTH;
                    break;
            }
        }
        return gameObjects;
    }

    private GameObject createPortal(float x, float y, StateChangePortal.PortalType type) {
        GameObject portal = new GameObject("Portal", new Transform(new Vector2(x, y)), 2);
        portal.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT * 2, true));
        portal.addComponent(new StateChangePortal(type));
        portal.addComponent(AssertPool.getSpritesheet("assets/portal.png").sprite.getFirst().copy());

        portal.setNonserializable();
        return portal;
    }

    private GameObject createBlock(float x, float y) {
        GameObject block = new GameObject("Block", new Transform(new Vector2(x, y)), 1);
        block.addComponent(AssertPool.getSpritesheet("assets/groundSprites.png").sprite.getFirst().copy());
        block.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        block.setNonserializable();
        return block;
    }

    private GameObject createSpike(float x, float y) {
        GameObject spike = new GameObject("Spike", new Transform(new Vector2(x, y)), 1);
        spike.addComponent(AssertPool.getSpritesheet("assets/spikes.png").sprite.getFirst().copy());
        spike.addComponent(new TriangleBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT));
        spike.setNonserializable();
        return spike;
    }

    private GameObject createJumpPad(float x, float y) {
        GameObject pad = new GameObject("JumpPad", new Transform(new Vector2(x, y)), 2);
        pad.addComponent(new BoxBounds(Constants.TILE_WIDTH, Constants.TILE_HEIGHT / 2.0f));
        pad.addComponent(new JumpPad(JumpPad.PadType.YELLOW_PAD));
        pad.setNonserializable();
        return pad;
    }
}