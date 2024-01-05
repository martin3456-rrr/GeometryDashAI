package com.UI;

import com.Component.Sprite;
import com.Component.Spritesheet;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.jade.Component;
import com.jade.GameObject;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainContainer extends Component {
    public List<GameObject> menuItems;
    public MainContainer()
    {
        this.menuItems = new ArrayList<>();
        init();
    }

    public void init() {
        Spritesheet groundSprites = AssertPool.getSpritesheet("assets/groundSprites.png");
        Spritesheet buttonSprites = AssertPool.getSpritesheet("assets/buttonSprites.png");

        for (int i = 0; i < groundSprites.sprite.size(); i++)
        {
            Sprite currentSprite  = groundSprites.sprite.get(i);
            int x = Constants.BUTTON_OFFSET_X + (currentSprite.column*Constants.BUTTON_WIDTH) + (currentSprite.column * Constants.BUTTON_SPACING_HZ);
            int y = Constants.BUTTON_OFFSET_Y + (currentSprite.row * Constants.BUTTON_HEIGHT) + (currentSprite.row * Constants.BUTTON_SPRACING_VT);

            GameObject obj = new GameObject("Generated", new Transform(new Vector2(x,y)));
            obj.addComponent(currentSprite.copy());
            MenuItem menuItem = new MenuItem(x,y,Constants.BUTTON_WIDTH,Constants.BUTTON_HEIGHT,buttonSprites.sprite.get(0),buttonSprites.sprite.get(1));
            obj.addComponent(menuItem);
            menuItems.add(obj);
        }
    }
    @Override
    public void start()
    {
        for(GameObject g: menuItems)
        {
            for(Component c :g.getAllComponents())
            {
                c.start();
            }
        }
    }
    @Override
    public void update(double dt)
    {
        for(GameObject g : this.menuItems)
        {
            g.update(dt);
        }
    }
    @Override
    public Component copy() {
        return null;
    }
    @Override
    public void draw(Graphics2D g2)
    {
        for(GameObject g : this.menuItems)
        {
            g.draw(g2);
        }
    }

    @Override
    public String serialize(int tabSize) {
        return "";
    }
}
