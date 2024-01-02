package com.jade;

import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {
    String name;
    Camera camera;
    List<GameObject> gameObject;
    Renderer renderer;
    public void Scene(String name)
    {
        this.name=name;
        this.camera= new Camera(new Vector2());
        this.gameObject = new ArrayList<>();
        this.renderer = new Renderer(this.camera);
        init();
    }
    public void init()
    {

    }
    public abstract  void update(double dt);
    public abstract void draw(Graphics2D g2);
}
