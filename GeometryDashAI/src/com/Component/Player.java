package com.Component;

import com.dataStructure.AssertPool;
import com.jade.Component;
import com.manager.AudioManager;
import com.util.Constants;
import com.jade.Window;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

public class Player extends Component {
    Sprite layerOne,layerTwo,layerThree,spaceship;
    public int width,height;
    public Rigidbody rb;
    public boolean onGround = true;
    public PlayerState state;
    public float gravityMultiplier = 1.0f;
    public float speedMultiplier = 1.0f;
    public float scaleMultiplier = 1.0f;
    public boolean isDead = false;
    public static boolean inSimulation = false;


    public Player(Sprite layerOne, Sprite layerTwo, Sprite layerThree, Color colorOne, Color colorTwo)
    {
        this.spaceship = AssertPool.getSprite("assets/player/spaceship.png");
        this.width = Constants.PLAYER_WIDTH;
        this.height= Constants.PLAYER_HEIGHT;
        this.layerOne=layerOne;
        this.layerTwo=layerTwo;
        this.layerThree=layerThree;
        this.state = PlayerState.NORMAL;
        int threshold = 200;
        for(int y= 0;y<layerOne.image.getWidth();y++)
        {
            for(int x = 0;x<layerOne.image.getHeight();x++)
            {
                Color color = new Color(layerOne.image.getRGB(x,y));
                if(color.getRed()>threshold && color.getGreen()>threshold && color.getBlue()>threshold)
                {
                    layerOne.image.setRGB(x,y,colorOne.getRGB());
                }
            }
        }
        for(int y= 0;y<layerTwo.image.getWidth();y++)
        {
            for(int x = 0;x<layerTwo.image.getHeight();x++)
            {
                Color color = new Color(layerTwo.image.getRGB(x,y));
                if(color.getRed()>threshold && color.getGreen()>threshold && color.getBlue()>threshold)
                {
                    layerTwo.image.setRGB(x,y,colorTwo.getRGB());
                }
            }
        }
    }
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody.class);
    }
    @Override
    public void update(double dt)
    {
        // Ta logika pozostaje bez zmian
        gameObject.transform.position.x += (float)(Constants.PLAYER_SPEED * speedMultiplier * dt);

        switch (state) {
            case NORMAL:
                handleNormalState();
                break;
            case FLYING:
                handleFlyingState(dt);
                break;
            case BALL:
                handleBallState();
                break;
            case UFO:
                handleUfoState();
                break;
            case WAVE:
                handleWaveState(dt);
                break;
        }

        gameObject.transform.scale.x = scaleMultiplier;
        gameObject.transform.scale.y = scaleMultiplier * gravityMultiplier;

        if(onGround && Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE))
        {
            if(state == PlayerState.NORMAL)
            {
                addJumpForce();
            }
            this.onGround = false;
        }
        if(PlayerState.FLYING == this.state && Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE))
        {
            addFlyForce();
            this.onGround = false;
        }
        if (this.state !=PlayerState.FLYING && !onGround)
        {
            gameObject.transform.rotation+= (float) (10.0f*dt);
        }
        else if(this.state !=PlayerState.FLYING)
        {
            gameObject.transform.rotation = (int)gameObject.transform.rotation % 360;
            if(gameObject.transform.rotation > 180)
            {
                gameObject.transform.rotation = 0;
            }
            else if(gameObject.transform.rotation > 0 && gameObject.transform.rotation < 180)
            {
                gameObject.transform.rotation = 0;
            }
        }
    }

    public void die()
    {
        this.isDead = true;
        if (!inSimulation) {
            gameObject.transform.position.x = 500;
            gameObject.transform.position.y = 350;
            AudioManager.play("death");
            gameObject.getComponent(Rigidbody.class).velocity.y = 0;
            gameObject.transform.rotation = 0;
            Window.getWindow().getCurrentScene().camera.position.x = 0;
            state = PlayerState.NORMAL;
        }
    }

    private void handleNormalState() {
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE) && onGround) {
            rb.velocity.y = Constants.JUMP_FORCE * gravityMultiplier;
            onGround = false;
        }
    }

    private void handleFlyingState(double dt) {
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            rb.velocity.y += (float) (Constants.FLY_FORCE * gravityMultiplier * dt);
        }
        float maxFlySpeed = 200.0f;
        if (Math.abs(rb.velocity.y) > maxFlySpeed) {
            rb.velocity.y = Math.signum(rb.velocity.y) * maxFlySpeed;
        }
    }

    private void handleBallState() {
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE) && onGround) {
            gravityMultiplier *= -1;
            rb.velocity.y = 0;
            onGround = false;
        }
    }

    private void handleUfoState() {
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            rb.velocity.y = Constants.JUMP_FORCE * 0.8f * gravityMultiplier;
        }
    }

    private void handleWaveState(double dt) {
        rb.velocity.y = 0;
        float waveSpeedY = 350.0f;

        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            gameObject.transform.position.y += (float) (waveSpeedY * gravityMultiplier * dt);
        } else {
            gameObject.transform.position.y -= (float) (waveSpeedY * gravityMultiplier * dt);
        }
    }
    private void addJumpForce()
    {
        gameObject.getComponent(Rigidbody.class).velocity.y = Constants.JUMP_FORCE;
        if (!inSimulation) {
            AudioManager.play("jump");
        }
    }
    private void addFlyForce()
    {
        gameObject.getComponent(Rigidbody.class).velocity.y = Constants.FLY_FORCE;
    }
    @Override
    public void draw(Graphics2D g2)
    {
        AffineTransform transform = new AffineTransform();
        transform.setToIdentity();
        transform.translate(gameObject.transform.position.x,gameObject.transform.position.y);
        transform.rotate(gameObject.transform.rotation,
                width*gameObject.transform.scale.x/2.0,height*gameObject.transform.scale.y/2.0);
        transform.scale(gameObject.transform.scale.x,gameObject.transform.scale.y);
        if(state == PlayerState.NORMAL) {
            g2.drawImage(layerOne.image, transform, null);
            g2.drawImage(layerTwo.image, transform, null);
            g2.drawImage(layerThree.image, transform, null);
        }
        else
        {
            transform.setToIdentity();
            transform.translate(gameObject.transform.position.x,
                    gameObject.transform.position.y);
            transform.rotate(gameObject.transform.rotation,
                    width*gameObject.transform.scale.x/4.0,height*gameObject.transform.scale.y/4.0);
            transform.scale(gameObject.transform.scale.x/2,gameObject.transform.scale.y/2);
            transform.translate(15,15);
            g2.drawImage(layerOne.image, transform, null);
            g2.drawImage(layerTwo.image, transform, null);
            g2.drawImage(layerThree.image, transform, null);


            transform.setToIdentity();
            transform.translate(gameObject.transform.position.x,gameObject.transform.position.y);
            transform.rotate(gameObject.transform.rotation,
                    width*gameObject.transform.scale.x/2.0,height*gameObject.transform.scale.y/2.0);
            transform.scale(gameObject.transform.scale.x,gameObject.transform.scale.y);
            g2.drawImage(spaceship.image,transform,null);
        }

    }
    @Override
    public Component copy() {
        return null;
    }
}
