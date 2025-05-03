package com.Component;

import com.jade.*;

public class Portal extends Component {
    public PlayerState stateChanger;
    public GameObject player;
    private BoxBounds bounds;

    public Portal(PlayerState stateChanger)
    {
        this.stateChanger = stateChanger;
    }
    public Portal(PlayerState stateChanger,GameObject player)
    {
        this.stateChanger = stateChanger;
        this.player = player;
    }
    @Override
    public void start()
    {
        this.bounds = gameObject.getComponent(BoxBounds.class);
        Scene scene = Window.getScene();
        if(scene instanceof LevelScene levelScene)
        {
            this.player = levelScene.player;
        }
    }
    @Override
    public void update(double dt)
    {
        if(player!=null) {
            if (player.getComponent(Player.class).state != stateChanger &&
                    BoxBounds.checkCollision(bounds, player.getComponent(BoxBounds.class))) {
                player.getComponent(Player.class).state = stateChanger;
            }
        }
    }
    @Override
    public Component copy() {
        return new Portal(this.stateChanger,this.player);
    }
}
