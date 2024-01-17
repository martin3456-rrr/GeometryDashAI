package com.jade;

import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    String name;
    public Camera camera;
    List<GameObject> gameObject;
    List<GameObject> objsRemove;
    Renderer renderer;
    public void Scene(String name)
    {
        this.name=name;
        this.camera= new Camera(new Vector2(1.0f,1.0f));
        this.gameObject = new ArrayList<>();
        this.objsRemove = new ArrayList<>();
        this.renderer = new Renderer(this.camera);
    }
    public void init()
    {

    }
    public List<GameObject> getAllGameObjects()
    {
        return gameObject;
    }
    public void removeGameObject(GameObject go)
    {
        objsRemove.add(go);
    }
    public void addGameObject(GameObject g)
    {
        gameObject.add(g);
        renderer.submit(g);
        for(Component c : g.getAllComponents())
        {
            c.start();
        }
    }
    public abstract  void update(double dt);
    public abstract void draw(Graphics2D g2);
}
