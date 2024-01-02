package com.jade;

import com.Component.*;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;

public class LevelEditorScene extends Scene {
    static LevelEditorScene currentScene;
    public GameObject player;
    GameObject ground;
    public LevelEditorScene(String name)
    {
        super.Scene(name);
    }
    public static LevelEditorScene getScene()
    {
        if(LevelEditorScene.currentScene == null)
        {
            LevelEditorScene.currentScene = new LevelEditorScene("Scene");
        }
        return  LevelEditorScene.currentScene;
    }
    @Override
    public void init() {
        player = new GameObject("Some game object",new Transform(new Vector2(500.0f,350.0f)));
        Spritesheet layerOne = new Spritesheet("assets/player/layerOne.png",42,42,2,13,13*5);
        Spritesheet layerTwo = new Spritesheet("assets/player/layerTwo.png",42,42,2,13,13*5);
        Spritesheet layerThree = new Spritesheet("assets/player/layerThree.png",42,42,2,13,13*5);
        Player playerComp = new Player(
                layerOne.sprite.get(0),
                layerTwo.sprite.get(0),
                layerThree.sprite.get(0),
                Color.RED,
                Color.GREEN);
        player.addComponent(playerComp);
        player.addComponent(new Rigidbody(new Vector2(30,0)));
        player.addComponent(new BoxBounds(Constants.PLAYER_WIDTH,Constants.PLAYER_HEIGHT));
        gameObject.add(player);
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)));
        ground.addComponent(new Ground());
        gameObject.add(ground);
        renderer.submit(player);
        renderer.submit(ground);
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
        for(GameObject g: gameObject)
        {
            g.update(dt);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
     g2.setColor(new Color(0.13f,0.1f,0.8f));
     g2.fillRect(0,0, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
     renderer.render(g2);
    }
}
