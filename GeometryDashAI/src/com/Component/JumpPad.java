package com.Component;

import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.util.Constants;
import java.awt.event.KeyEvent;

public class JumpPad extends Component {
    public enum PadType {
        YELLOW_PAD, // Wyższy skok, automatyczny
        BLUE_PAD,   // Niższy skok, automatyczny
        YELLOW_RING, // Wyższy skok, wymaga kliknięcia
        BLUE_RING   // Niższy skok, wymaga kliknięcia
    }

    private PadType type;
    private BoxBounds triggerBounds;
    private float jumpForce;
    private boolean requiresClick;

    public JumpPad(PadType type) {
        this.type = type;
        this.requiresClick = false;

        switch (type) {
            case YELLOW_PAD:
                this.jumpForce = Constants.JUMP_FORCE * 1.2f; // Silniejszy niż normalny skok
                break;
            case BLUE_PAD:
                this.jumpForce = Constants.JUMP_FORCE * 0.8f; // Słabszy
                break;
            case YELLOW_RING:
                this.jumpForce = Constants.JUMP_FORCE * 1.2f;
                this.requiresClick = true;
                break;
            case BLUE_RING:
                this.jumpForce = Constants.JUMP_FORCE * 0.8f;
                this.requiresClick = true;
                break;
        }
    }

    @Override
    public void start() {
        this.triggerBounds = gameObject.getComponent(BoxBounds.class);
        if (this.triggerBounds != null) {
            this.triggerBounds.isTrigger = true;
        }
    }

    @Override
    public void update(double dt) {
        GameObject player = Window.getScene().getAllGameObjects().stream()
                .filter(go -> go.getComponent(Player.class) != null)
                .findFirst().orElse(null);

        if (player != null && triggerBounds != null && BoxBounds.checkCollision(triggerBounds, player.getComponent(BoxBounds.class))) {
            boolean canJump = false;
            if (requiresClick) {
                if (Window.keyListener().IsKeyPressed(KeyEvent.VK_SPACE)) {
                    canJump = true;
                }
            } else {
                canJump = true;
            }

            if (canJump) {
                Rigidbody playerRb = player.getComponent(Rigidbody.class);
                playerRb.velocity.y = this.jumpForce;
            }
        }
    }

    @Override
    public Component copy() {
        return new JumpPad(this.type);
    }
}