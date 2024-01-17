package com.jade;

import com.Component.*;
import com.File.Parser;
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

        mouseCursor = new GameObject("Mouse Cursor",new Transform(new Vector2()),10);
        mouseCursor.addComponent(new LevelEditorControls(Constants.TILE_WIDTH,Constants.TILE_HEIGHT));

        player = new GameObject("Some game object",new Transform(new Vector2(500.0f,350.0f)),0);
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

        player.setNonserializable();
        addGameObject(player);

        initBackgrounds();
    }
    public void initAssetPool()
    {
        AssertPool.addSpritesheet("assets/player/layerOne.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerTwo.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/player/layerThree.png",42,42,2,13,13*5);
        AssertPool.addSpritesheet("assets/groundSprites.png", 42, 42, 2, 6, 12);
        AssertPool.addSpritesheet("assets/ui/buttonSprites.png", 60, 60, 2, 2, 2);
        AssertPool.addSpritesheet("assets/ui/tabs.png",Constants.TAB_WIDTH,Constants.TAB_HEIGHT,2,6,6);
        AssertPool.addSpritesheet("assets/spikes.png",42,42,2,6,4);
        AssertPool.addSpritesheet("assets/bigSprites.png",84,84,2,2,2);
        AssertPool.addSpritesheet("assets/smallBlocks.png",42,42,2,6,1);
        AssertPool.addSpritesheet("assets/portal.png",44,85,2,2,2);

    }
    public void initBackgrounds()
    {
        ground = new GameObject("Ground",new Transform(
                new Vector2(0,Constants.GROUND_Y)),1);
        ground.addComponent(new Ground());
        ground.setNonserializable();
        addGameObject(ground);
        int numBackground = 5;
        GameObject[] backgrounds = new GameObject[numBackground];
        GameObject[] groundBgs = new GameObject[numBackground];
        for(int i = 0;i<numBackground;i++)
        {
            ParallaxBackgrounds bg = new ParallaxBackgrounds("assets/backgrounds/bg01.png",
                    null,ground.getComponent(Ground.class),false);
            int x = i*bg.sprite.width;
            int y = 0;
            GameObject go = new GameObject("Background",new Transform(new Vector2(x,y)),-10);
            go.setUI(true);
            go.addComponent(bg);
            go.setNonserializable();
            backgrounds[i] = go;
            ParallaxBackgrounds groundBg = new ParallaxBackgrounds("assets/grounds/ground01.png",
                    null,ground.getComponent(Ground.class),true);
            x = i*groundBg.sprite.width;
            y = (int)ground.transform.position.y;
            GameObject groundGo = new GameObject("GroundBg",new Transform(new Vector2(x,y)),-9);
            groundGo.addComponent(groundBg);
            groundGo.setUI(true);
            groundGo.setNonserializable();
            groundBgs[i] = groundGo;

            addGameObject(go);
            addGameObject(groundGo);
        }

    }

    @Override
    public void update(double dt) {
        if(camera.position.y >Constants.CAMERA_OFFSET_GRAOUND_Y+70)
        {
            camera.position.y = Constants.CAMERA_OFFSET_GRAOUND_Y+70;
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
        else if(Window.getWindow().keyLister.IsKeyPressed(KeyEvent.VK_F2))
        {
            importLevel("Test");
        }
        else if(Window.getWindow().keyLister.IsKeyPressed(KeyEvent.VK_F3))
        {
            Window.getWindow().changeScene(1);
        }
        if(objsRemove.size() > 0) {
            for (GameObject go : objsRemove) {
                gameObject.remove(go);
                renderer.gameObjects.get(go.zIndex).remove(go);
            }
            objsRemove.clear();
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
     g2.setColor(Constants.BG_COLOR);
     g2.fillRect(0,0, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
     renderer.render(g2);
     grid.draw(g2);
     editingButtons.draw(g2);
     mouseCursor.draw(g2);
    }
}
