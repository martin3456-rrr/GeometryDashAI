package com.UI;

import com.Component.Sprite;
import com.jade.Component;
import com.jade.Window;

import java.awt.*;
import java.awt.event.MouseEvent;

public class TabItem extends Component {
    private int x,y,width,height;
    private Sprite sprite;
    public boolean isSelected;
    private MainContainer parentContainer;
    public TabItem(int x, int y, int width, int height, Sprite sprite,MainContainer parent)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        this.isSelected = false;
        this.parentContainer = parent;
    }

    @Override
    public void update(double dt)
    {
        if(Window.getWindow().MouseListener.mousePressed && Window.getWindow().MouseListener.mouseButton == MouseEvent.BUTTON1)
        {
            if(!isSelected && Window.getWindow().MouseListener.x>this.x && Window.getWindow().MouseListener.x<=this.x + this.width &&
                    Window.getWindow().MouseListener.y > this.y && Window.getWindow().MouseListener.y <= this.y + this.height)
            {
                //Click insider the button
                isSelected = true;
                this.parentContainer.setHotTab(gameObject);
            }
        }
    }
    @Override
    public void draw(Graphics2D g2)
    {
        if(isSelected)
        {
            g2.drawImage(sprite.image,x,y,width,height,null);
        }
        else
        {
            float alpha = 0.5f;
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
            g2.setComposite(ac);
            g2.drawImage(sprite.image,x,y,width,height,null);
            alpha = 1.0f;
            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
            g2.setComposite(ac);
        }
    }
    @Override
    public Component copy() {
        return null;
    }
}
