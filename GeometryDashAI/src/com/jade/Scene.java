package com.jade;

import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Scene {
    public GameObject player;
    String name;
    public Camera camera;
    List<GameObject> gameObject;
    List<GameObject> objsRemove;
    List<GameObject> objsToAdd;
    Renderer renderer;

    private boolean pendingModifications = false;
    private boolean mirrored;

    public void Scene(String name)
    {
        this.name=name;
        this.camera = new Camera(new Vector2());
        this.gameObject = new ArrayList<>();
        this.objsRemove = new ArrayList<>();
        this.objsToAdd = new ArrayList<>();
        this.renderer = new Renderer(this.camera);
    }
    public abstract void init();
    public List<GameObject> getAllGameObjects()
    {
        return gameObject;
    }
    public void removeGameObject(GameObject go)
    {
        objsRemove.add(go);
        pendingModifications = true;
    }
    public void addGameObject(GameObject g) {
        gameObject.add(g);
        renderer.submit(g);
        for(Component c : g.getAllComponents())
        {
            c.start();
        }
    }

    private void addPendingObjects() {
        for (GameObject g : objsToAdd) {
            gameObject.add(g);
            g.setScene(this); // Set scene reference
            renderer.submit(g);
            for (Component c : g.getAllComponents()) {
                c.start();
            }
        }
        objsToAdd.clear();
    }

    private void removePendingObjects() {
        for (GameObject go : objsRemove) {
            gameObject.remove(go);
            renderer.remove(go);
        }
        objsRemove.clear();
    }

    protected void processModifications() {
        if (!pendingModifications) {
            return;
        }

        addPendingObjects();
        removePendingObjects();

        pendingModifications = false;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;

    }

    public abstract void update(double dt);
    public abstract void draw(Graphics2D g2);
    protected Iterator<GameObject> getGameObjectIterator() {
        return gameObject.iterator();
    }
}
