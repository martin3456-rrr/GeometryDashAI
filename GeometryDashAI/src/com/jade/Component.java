package com.jade;

import java.awt.*;

public abstract class Component<T> {
    public GameObject gameObject;
    public  void update(double dt)
    {
        return;
    }
    public void draw(Graphics2D g2)
    {
       return;
    }
    public void start()
    {
        return;
    }
    public abstract Component copy();

}
