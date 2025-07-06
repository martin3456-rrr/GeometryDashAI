package com.Component;

import com.dataStructure.AssertPool;
import com.jade.Component;
import com.util.Constants;
import com.jade.Window;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

public class Player extends Component {
    public enum Speed { SLOW, NORMAL, FAST }
    Sprite layerOne,layerTwo,layerThree,spaceship;
    public int width,height;
    public Rigidbody rb;
    public boolean onGround = true;
    public PlayerState state;
    public float gravityMultiplier = 1.0f;
    public float speedMultiplier = 1.0f;
    public float scaleMultiplier = 1.0f;
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

    /**
     * @param dt
     */
    @Override
    public void update(float dt) {

    }

    @Override
    public void update(double dt)
    {
        // Zastosuj prędkość
        gameObject.transform.position.x += (float)(Constants.PLAYER_SPEED * speedMultiplier * dt);

        switch (state) {
            case NORMAL:
                handleNormalState();
                break;
            case FLYING:
                handleFlyingState(dt); // Przekaż dt jako parametr
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
            if(gameObject.transform.rotation > 180 && gameObject.transform.rotation < 360)
            {
                gameObject.transform.rotation = 0;
            }
            else if(gameObject.transform.rotation > 0 && gameObject.transform.rotation < 180)
            {
                gameObject.transform.rotation = 0;
            }
        }
    }

    private void handleNormalState() {
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE) && onGround) {
            rb.velocity.y = Constants.JUMP_FORCE * gravityMultiplier;
            onGround = false;
        }
    }

    private void handleFlyingState(double dt) { // Dodaj parametr dt
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            rb.velocity.y += Constants.FLY_FORCE * gravityMultiplier * dt;
        }
        // Ograniczenie prędkości w osi Y, aby statek nie leciał za szybko
        float maxFlySpeed = 200.0f;
        if (Math.abs(rb.velocity.y) > maxFlySpeed) {
            rb.velocity.y = Math.signum(rb.velocity.y) * maxFlySpeed;
        }
    }

    private void handleBallState() {
        // Kula zmienia grawitację po kliknięciu, tylko gdy dotyka podłoża/sufitu
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE) && onGround) {
            gravityMultiplier *= -1;
            rb.velocity.y = 0; // Mały "kop" w nowym kierunku
            onGround = false;
        }
    }

    private void handleUfoState() {
        // UFO wykonuje mały podskok w powietrzu po każdym kliknięciu (jak Flappy Bird)
        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            rb.velocity.y = Constants.JUMP_FORCE * 0.8f * gravityMultiplier; // Nieco słabszy skok
        }
    }

    private void handleWaveState(double dt) {
        // Wave porusza się po linii prostej i nie podlega grawitacji. Zmienia kierunek po kliknięciu.
        rb.velocity.y = 0; // Wyłączamy grawitację dla tego trybu
        float waveSpeedY = 350.0f;

        if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
            // Ruch w górę (lub w dół, jeśli grawitacja jest odwrócona)
            gameObject.transform.position.y += waveSpeedY * gravityMultiplier * dt;
        } else {
            // Ruch w dół (lub w górę...)
            gameObject.transform.position.y -= waveSpeedY * gravityMultiplier * dt;
        }
    }
    private void addJumpForce()
    {
        gameObject.getComponent(Rigidbody.class).velocity.y = Constants.JUMP_FORCE;
    }
    private void addFlyForce()
    {
        gameObject.getComponent(Rigidbody.class).velocity.y = Constants.FLY_FORCE;
    }
    public void die()
    {
        gameObject.transform.position.x = 500;
        gameObject.transform.position.y = 350;
        gameObject.getComponent(Rigidbody.class).velocity.y = 0;
        gameObject.transform.rotation = 0;
        Window.getWindow().getCurrentScene().camera.position.x = 0;
        state = PlayerState.NORMAL;
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
    public void setSpeed(Speed speed) { /* implementacja zmiany prędkości */ }
    public void setMirrored(boolean isMirrored) { /* implementacja lustrzanego odbicia */ }
    public void setMini(boolean isMini) { /* implementacja zmniejszonego rozmiaru */ }
    @Override
    public Component copy() {
        return null;
    }
}