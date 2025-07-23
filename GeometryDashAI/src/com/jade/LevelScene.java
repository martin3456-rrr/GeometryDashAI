package com.jade;

import com.Component.*;
import com.Generator.*;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.manager.AudioManager;
import com.manager.Difficulty;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class LevelScene extends Scene {
    public GameObject player;
    public BoxBounds playerBounds;
    public LevelScene(String name) {
        super.Scene(name);
    }
    @Override
    public void init() {
        initAssetPool();
        initAudioManager();
        initPlayer();
        initBackgroundsAndGround();

        if (Window.selectedMode == Window.GameMode.AI_GENERATED) {
            System.out.println("Generating new level with AI...");
            LevelGenerationConfig config = new LevelGenerationConfig(
                    Window.selectedDifficulty,
                    Constants.DEFAULT_LEVEL_LENGTH*2,
                    Set.of(),
                    1.0,
                    Window.selectedModelType
            );
            GeneticLevelGenerator generator = new GeneticLevelGenerator();
            LevelChromosome bestLevel = generator.generateBestLevel(config);
            GeneratedLevelLoader levelLoader = new GeneratedLevelLoader();
            List<GameObject> levelObjects = levelLoader.translateChromosomeToGameObjects(bestLevel);
            for (GameObject obj : levelObjects) {
                addGameObject(obj);
            }
            Window.generatedChromosome = null;
        } else if (Window.selectedMode == Window.GameMode.ORIGINAL_LEVEL) {
            System.out.println("Loading original level: " + Window.levelToLoad);
            OriginalLevelLoader loader = new OriginalLevelLoader();
            List<GameObject> levelObjects = loader.loadLevel(Window.levelToLoad);
            for (GameObject obj : levelObjects) {
                addGameObject(obj);
            }
        }
    }

    private void initAudioManager() {
        AudioManager.addSound("jump", "assets/sounds/jump.wav");
        AudioManager.addSound("death", "assets/sounds/death.wav");
        AudioManager.loop("levelMusic");
    }
    private void initPlayer() {
        float x = 120.0f;
        float y = Constants.GROUND_Y - Constants.PLAYER_HEIGHT ;
        player = new GameObject("Player", new Transform(new Vector2(x, y)), 0);

        Spritesheet layerOne = AssertPool.getSpritesheet("assets/player/layerOne.png");
        Spritesheet layerTwo = AssertPool.getSpritesheet("assets/player/layerTwo.png");
        Spritesheet layerThree = AssertPool.getSpritesheet("assets/player/layerThree.png");

        if (layerOne == null || layerTwo == null || layerThree == null ||
                layerOne.sprite.isEmpty() || layerTwo.sprite.isEmpty() || layerThree.sprite.isEmpty()) {
            player.addComponent(new Sprite("assets/player/spaceship.png"));
        } else {
            Player playerComp = new Player(layerOne.sprite.getFirst(), layerTwo.sprite.getFirst(), layerThree.sprite.getFirst(), Color.RED, Color.GREEN);
            player.addComponent(playerComp);
        }
        player.addComponent(new Rigidbody(new Vector2(Constants.PLAYER_SPEED, 0)));
        playerBounds = new BoxBounds(Constants.PLAYER_WIDTH , Constants.PLAYER_HEIGHT );
        player.addComponent(playerBounds);
        addGameObject(player);
    }

    private void initBackgroundsAndGround() {
        GameObject ground;
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)),1);
        ground.addComponent(new Ground());
        addGameObject(ground);
        int numBackground = 7;
        GameObject[] backgrounds = new GameObject[numBackground];
        GameObject[] groundBgs = new GameObject[numBackground];
        for(int i = 0;i<numBackground;i++)
        {
            ParallaxBackgrounds bg = new ParallaxBackgrounds("assets/backgrounds/bg01.png",backgrounds,ground.getComponent(Ground.class),false);
            int x = i*bg.sprite.width;
            int y = 0;
            GameObject go = new GameObject("Background",new Transform(new Vector2(x,y)),-10);
            go.setUI(true);
            go.addComponent(bg);
            backgrounds[i] = go;

            ParallaxBackgrounds groundBg = new ParallaxBackgrounds("assets/grounds/ground01.png",groundBgs,ground.getComponent(Ground.class),true);
            x = i*groundBg.sprite.width;
            y = 0;
            GameObject groundGo = new GameObject("GroundBg",new Transform(new Vector2(x,y)),-9);
            groundGo.addComponent(groundBg);
            groundGo.setUI(true);
            groundBgs[i] = groundGo;
            addGameObject(go);
            addGameObject(groundGo);
        }
    }

    private void initAssetPool() {
        AssertPool.addSpritesheet("assets/player/layerOne.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerTwo.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerThree.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/groundSprites.png", 42, 42, 2, 6, 12);
        AssertPool.getSprite("assets/player/spaceship.png");

        AssertPool.addSpritesheet("assets/spikes.png",42,42,2,6,4);
        AssertPool.addSpritesheet("assets/bigSprites.png",84,84,2,2,2);
        AssertPool.addSpritesheet("assets/smallBlocks.png",42,42,2,6,6);
        AssertPool.addSpritesheet("assets/portal.png",44,85,2,2,2);
    }

    @Override
    public void update(double dt) {
        if (player == null) return;
        updateCameraPosition();

        player.update(dt);
        Player pc = player.getComponent(Player.class);
        if (pc != null) pc.onGround = false;


        Iterator<GameObject> it = gameObject.iterator();
        while (it.hasNext()) {
            GameObject go = it.next();
            if (go == player) {
                continue;
            }
            go.update(dt);

            Bounds b = go.getComponent(Bounds.class);
            if (b != null && Bounds.checkCollision(playerBounds, b)) {
                Bounds.resolveCollision(b, player);
            }

            if (go.transform.position.x + Constants.TILE_WIDTH < camera.position.x - Constants.TILE_WIDTH * 5
                    && !go.isUI) {
                it.remove();
                renderer.remove(go);
            }
        }
    }
    private void updateCameraPosition() {
        if(player.transform.position.x - camera.position.x>Constants.CAMERA_OFFSET_X)
        {
            camera.position.x = player.transform.position.x - Constants.CAMERA_OFFSET_X;
        }
        camera.position.y = player.transform.position.y - Constants.CAMERA_OFFSET_Y;
        if(camera.position.y >Constants.CAMERA_OFFSET_GROUND_Y)
        {
            camera.position.y = Constants.CAMERA_OFFSET_GROUND_Y;
        }
    }
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Constants.BG_COLOR);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        renderer.render(g2);
    }
    @Override
    public void removeGameObject(GameObject go) {
        if (objsRemove == null) objsRemove = new ArrayList<>();
        if (!objsRemove.contains(go)) objsRemove.add(go);
    }
}