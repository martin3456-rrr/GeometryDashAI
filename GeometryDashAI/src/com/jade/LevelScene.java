package com.jade;

import com.Component.*;
import com.File.Parser;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;

public class LevelScene extends Scene{
    static LevelScene currentScene;
    public GameObject player;
    public BoxBounds playerBounds;
    public LevelScene(String name)
    {
        super.Scene(name);
    }

    @Override
    public void init() {
        initAssetPool();
        player = new GameObject("Some game object",new Transform(new Vector2(500.0f,350.0f)));
        Spritesheet layerOne = AssertPool.getSpritesheet("assets/player/layerOne.png");
        Spritesheet layerTwo = AssertPool.getSpritesheet("assets/player/layerTwo.png");
        Spritesheet layerThree = AssertPool.getSpritesheet("assets/player/layerThree.png");
        Player playerComp = new Player(
                layerOne.sprite.get(0),
                layerTwo.sprite.get(0),
                layerThree.sprite.get(0),
                Color.RED,
                Color.GREEN);
        player.addComponent(playerComp);
        player.addComponent(new Rigidbody(new Vector2(Constants.PLAYER_SPEED,0)));
        player.addComponent(new BoxBounds(Constants.PLAYER_WIDTH,Constants.PLAYER_HEIGHT));
        playerBounds = new BoxBounds(Constants.TILE_WIDTH,Constants.TILE_HEIGHT);
        player.addComponent(playerBounds);


        renderer.submit(player);

        initBackgrounds();
        importLevel("Test");
    }
    public void initBackgrounds()
    {
        GameObject ground;
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)));
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
            GameObject go = new GameObject("Background",new Transform(new Vector2(x,y)));
            go.setUI(true);
            go.addComponent(bg);
            backgrounds[i] = go;
            ParallaxBackgrounds groundBg = new ParallaxBackgrounds("assets/grounds/ground01.png",groundBgs,ground.getComponent(Ground.class),true);
            x = i*groundBg.sprite.width;
            y = bg.sprite.height;
            GameObject groundGo = new GameObject("GroundBg",new Transform(new Vector2(x,y)));
            groundGo.addComponent(groundBg);
            groundGo.setUI(true);
            groundBgs[i] = groundGo;

            addGameObject(go);
            addGameObject(groundGo);
        }

    }
    public void initAssetPool()
    {
        AssertPool.addSpritesheet("assets/player/layerOne.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerTwo.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerThree.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/groundSprites.png", 42, 42, 2, 6, 12);
    }
    @Override
    public void update(double dt) {
        if(player.transform.position.x - camera.position.x>Constants.CAMERA_OFFSET_X)
        {
            camera.position.x = player.transform.position.x - Constants.CAMERA_OFFSET_X;
        }
        if(player.transform.position.y - camera.position.y>Constants.CAMERA_OFFSET_Y)
        {
            camera.position.y = player.transform.position.y - Constants.CAMERA_OFFSET_Y;
        }
        if(camera.position.y >Constants.CAMERA_OFFSET_GRAOUND_Y)
        {
            camera.position.y = Constants.CAMERA_OFFSET_GRAOUND_Y;
        }
        player.update(dt);
        player.getComponent(Player.class).onGround = false;
        for(GameObject g: gameObject)
        {
            g.update(dt);
            Bounds b = g.getComponent(Bounds.class);
            if(b!=null)
            {
                if(BoxBounds.checkCollision(playerBounds,b))
                {
                   Bounds.resolveCollision(b,player);
                }
            }
        }
    }
    private void importLevel(String filename)
    {
        Parser.openFile(filename);

        GameObject go = Parser.parseGameObject();
        while(go!=null)
        {
            addGameObject(go);
            go = Parser.parseGameObject();
        }
    }
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Constants.BG_COLOR);
        g2.fillRect(0,0, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        renderer.render(g2);
    }
}
