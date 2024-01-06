package com.jade;

import com.Component.*;
import com.UI.MainContainer;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LevelEditorScene extends Scene {
    public GameObject player;
    private GameObject ground;
    private Grid grid;
    public CameraControls cameraControls;
    public GameObject mouseCursor;
    private MainContainer editingButtons;
    public LevelEditorScene(String name)
    {
        super.Scene(name);

    }
    @Override
    public void init() {
        initAssetPool();
        editingButtons = new MainContainer();
        grid = new Grid();
        cameraControls = new CameraControls();
        editingButtons.start();

        mouseCursor = new GameObject("Mouse Cursor",new Transform(new Vector2()));
        mouseCursor.addComponent(new SnapToGrid(Constants.TILE_WIDTH,Constants.TILE_HEIGHT));

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
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)));
        ground.addComponent(new Ground());
        ground.setNonserializable();
        player.setNonserializable();
        addGameObject(player);
        addGameObject(ground);
    }
    public void initAssetPool()
    {
        AssertPool.addSpritesheet("assets/player/layerOne.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerTwo.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerThree.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/groundSprites.png", 42, 42, 2, 6, 12);
        AssertPool.addSpritesheet("assets/buttonSprites.png", 60, 60, 2, 2, 2);
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

        if(Window.getWindow().keyLister.IsKeyPressed(KeyEvent.VK_F1))
        {
            export("Test");
        }
    }
    private void export(String filename)
    {
        try{
            FileOutputStream fos = new FileOutputStream("levels/"+filename+".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.putNextEntry(new ZipEntry(filename+".json"));
            int i = 0;
            for(GameObject go:gameObject)
            {
                String str = go.serialize(0);
                if(str.compareTo("")!=0)
                {
                    zos.write(str.getBytes());
                    if(i!=gameObject.size()-1)
                    {
                        zos.write(",\n".getBytes());
                    }
                }
                i++;
            }
            zos.closeEntry();
            zos.close();
            fos.close();

        }catch(IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
     g2.setColor(new Color(0.13f,0.1f,0.8f));
     g2.fillRect(0,0, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
     renderer.render(g2);
     grid.draw(g2);
     editingButtons.draw(g2);
     mouseCursor.draw(g2);
    }
}
