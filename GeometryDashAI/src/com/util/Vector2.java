package com.util;



public class Vector2 {
    public float x,y;
    public Vector2(float x,float y)
    {
        this.x=x;
        this.y=y;
    }
    public Vector2()
    {
        this.x=0;
        this.y=0;
    }
    public Vector2 copy()
    {
        return new Vector2(this.x,this.y);
    }

    public float distance(Vector2 other) {
        float dx = this.x - other.x;
        float dy = this.y - other.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
