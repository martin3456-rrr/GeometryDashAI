package com.Component;

import com.jade.Component;
import com.jade.GameObject;
import com.jade.LevelEditorScene;
import com.util.Constants;

import java.awt.*;

public class Ground extends Component {
    @Override
    public void update(double dt)
    {
        GameObject player = LevelEditorScene.getScene().player;
        if(player.transform.position.y + player.getComponent(BoxBounds.class).height >
        gameObject.transform.position.y)
        {
            player.transform.position.y = gameObject.transform.position.y - player.getComponent(BoxBounds.class).height;
        }
    }
    @Override
    public void draw(Graphics2D g2)
    {
        g2.setColor(Color.BLACK);
        g2.drawRect((int)gameObject.transform.position.x-10,(int)gameObject.transform.position.y,
                Constants.SCREEN_WIDTH+20,Constants.SCREEN_HEIGHT);
    }
}
