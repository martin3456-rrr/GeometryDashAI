package com.Component;

import com.jade.Component;
import com.util.Constants;
import com.util.Vector2;

public class Rigidbody extends Component {
    public Vector2 velocity;
    public Rigidbody(Vector2 vel)
    {
        this.velocity = vel;
    }
    @Override
    public void update(double dt)
    {
        gameObject.transform.position.y+= (float) (velocity.y*dt);
        gameObject.transform.position.x+= (float) (velocity.x*dt);
        Player playerComp = gameObject.getComponent(Player.class);
        float currentGravity = Constants.GRAVITY;
        if (playerComp != null) {
            currentGravity *= playerComp.gravityMultiplier;
        }
        velocity.y += (float) (currentGravity * dt);

        if(Math.abs(velocity.y)>Constants.TERMINAL_VELOCITY)
        {
            velocity.y = Math.signum(velocity.y)* Constants.TERMINAL_VELOCITY;
        }
    }
    @Override
    public Component copy() {
        return null;
    }
}
