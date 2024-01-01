package com.dataStructure;

import com.util.Vector2;


public class Transform {
    public Vector2 position;
    public Vector2 scale;
    public float rotation;

    public Transform(Vector2 position)
    {
        this.position=position;
        this.scale = new Vector2(1f,1f);
        this.rotation=0.0f;
    }
    @Override
    public String toString()
    {
        return "Position ("+position.x+", "+position.y+")";
    }
}
