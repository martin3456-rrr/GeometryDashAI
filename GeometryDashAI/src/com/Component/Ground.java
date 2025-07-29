package com.Component;

import com.jade.*;
import com.jade.Component;
import com.jade.Window;
import com.util.Constants;

import java.awt.*;

public class Ground extends Component {
    @Override
    public void update(double dt)
    {
        if(!Window.getWindow().isInEditor) {
            LevelScene scene = (LevelScene) Window.getWindow().getCurrentScene();
            GameObject player = scene.player;
            if(player != null) {
                BoxBounds playerBounds = player.getComponent(BoxBounds.class);
                Player playerComponent = player.getComponent(Player.class);
                if(playerBounds != null) {
                    if (player.transform.position.y + player.getComponent(BoxBounds.class).height >
                            gameObject.transform.position.y) {
                        player.transform.position.y = gameObject.transform.position.y - (float) player.getComponent(BoxBounds.class).height;
                        if(playerComponent!=null)
                        {
                            player.getComponent(Player.class).onGround = true;
                        }
                    }
                }
            }
            gameObject.transform.position.x = scene.camera.position.x - 10;
        }
        else
        {
            gameObject.transform.position.x = Window.getWindow().getCurrentScene().camera.position.x - 10;
        }
    }
    @Override
    public void draw(Graphics2D g2)
    {
        g2.setColor(Color.BLACK);
        g2.drawRect((int)gameObject.transform.position.x,(int)gameObject.transform.position.y,
                Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
    }
    @Override
    public Component copy() {
        return this;
    }
}
