package com.jade;

import com.Component.*;
import com.UI.MainContainer;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;

public class LevelEditorScene extends Scene {
    public GameObject player;
    private GameObject ground;
    private Grid grid;
    private CameraControls cameraControls;
    public GameObject mouseCursor;
    private MainContainer editingButtons = new MainContainer();
    public LevelEditorScene(String name)
    {
        super.Scene(name);

    }
    @Override
    public void init() {
        grid = new Grid();
        cameraControls = new CameraControls();
        editingButtons.start();
        mouseCursor = new GameObject("Mouse Cursor",new Transform(new Vector2()));
        mouseCursor.addComponent(new SnapToGrid(Constants.TILE_WIDTH,Constants.TILE_HEIGHT));
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
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)));
        ground.addComponent(new Ground());
        addGameObject(player);
        addGameObject(ground);
    }

    @Override
    public void update(double dt) {
        if(camera.position.y >Constants.CAMERA_OFFSET_GRAOUND_Y)
        {
            camera.position.y = Constants.CAMERA_OFFSET_GRAOUND_Y;
        }
        for(GameObject g: gameObject)
        {
            g.update(dt);
        }
        cameraControls.update(dt);
        grid.update(dt);
        editingButtons.update(dt);
        mouseCursor.update(dt);
    }

    @Override
    public void draw(Graphics2D g2) {
     g2.setColor(new Color(0.13f,0.1f,0.8f));
     g2.fillRect(0,0, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
     renderer.render(g2);
     grid.draw(g2);
     editingButtons.draw(g2);
     //Should be draw last
     mouseCursor.draw(g2);
    }
}
