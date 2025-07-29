package com.Component;

import com.jade.Component;
import com.jade.GameObject;
import com.util.Vector2;

enum BounsType
{
    Box,
    Triangle
}

public abstract class Bounds extends Component {
    public BounsType type;
    public boolean isSelected;

    abstract public float getWidth();
    abstract public float getHeight();

    abstract public boolean raycast(Vector2 position);

    public static boolean checkCollision(Bounds b1,Bounds b2)
    {
        //We know that at least 1 bounds will always be a box
        if(b1.type == b2.type && b1.type == BounsType.Box)
        {
            return BoxBounds.checkCollision((BoxBounds)b1,(BoxBounds)b2);
        }
        else if(b1.type == BounsType.Box && b2.type==BounsType.Triangle)
        {
            return TriangleBounds.checkCollision((BoxBounds)b1,(TriangleBounds)b2);
        }
        else if(b1.type == BounsType.Triangle && b2.type == BounsType.Box)
        {
            return TriangleBounds.checkCollision((BoxBounds)b2,(TriangleBounds)b1);
        }
        return false;
    }
    
    public static void resolveCollision(Bounds b, GameObject plr)
    {
        if(b.type == BounsType.Box)
        {
            BoxBounds box = (BoxBounds) b;
            box.resolveCollision(plr);
        }
        else if(b.type == BounsType.Triangle)
        {
            Player playerComponent = plr.getComponent(Player.class);
            if (playerComponent != null) {
                playerComponent.die();
            }
        }
    }
}