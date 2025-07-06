package com.Component;

import com.jade.*;

public class StateChangePortal extends Component {
    public enum PortalType {
        SHIP, BALL, UFO, WAVE,
        GRAVITY_UP, GRAVITY_DOWN,
        SPEED_SLOW, SPEED_NORMAL, SPEED_FAST,
        MIRROR_ON, MIRROR_OFF,
        MINI_ON, MINI_OFF,
        DUAL_ON, CUBE, SPEED_VERY_FAST, DUAL_OFF
    }

    private PortalType type;
    private BoxBounds triggerBounds;
    private GameObject player;

    public StateChangePortal(PortalType type) {
        this.type = type;
    }

    @Override
    public void start() {
        this.triggerBounds = gameObject.getComponent(BoxBounds.class);
        if (this.triggerBounds != null) {
            this.triggerBounds.isTrigger = true;
        }

        Scene scene = Window.getScene();
        if (scene instanceof LevelScene levelScene) {
            this.player = levelScene.player;
        }
    }

    @Override
    public void update(float dt) {
        GameObject playerObj = Window.getScene().player;
        if (playerObj != null && triggerBounds != null && BoxBounds.checkCollision(triggerBounds, playerObj.getComponent(BoxBounds.class))) {
            Player player = playerObj.getComponent(Player.class);
            if (player == null) return;

            switch(this.type) {
                // Tryby Gry
                case CUBE: player.state = PlayerState.NORMAL; break;
                case SHIP: player.state = PlayerState.FLYING; break;
                case BALL: player.state = PlayerState.BALL; break;
                case UFO: player.state = PlayerState.UFO; break;
                case WAVE: player.state = PlayerState.WAVE; break;

                // Grawitacja
                case GRAVITY_UP: player.gravityMultiplier = -1.0f; break;
                case GRAVITY_DOWN: player.gravityMultiplier = 1.0f; break;

                // Prędkość
                case SPEED_SLOW: player.speedMultiplier = 0.8f; break;
                case SPEED_NORMAL: player.speedMultiplier = 1.0f; break;
                case SPEED_FAST: player.speedMultiplier = 1.2f; break;
                case SPEED_VERY_FAST: player.speedMultiplier = 1.5f; break;

                // Rozmiar
                case MINI_ON: player.scaleMultiplier = 0.5f; break;
                case MINI_OFF: player.scaleMultiplier = 1.0f; break;

                // Lustro (wymaga modyfikacji w renderowaniu)
                case MIRROR_ON: Window.getScene().setMirrored(true); break;
                case MIRROR_OFF: Window.getScene().setMirrored(false); break;
            }
            // Portal działa tylko raz
            this.gameObject.removeComponent(StateChangePortal.class);
        }
    }

    @Override
    public Component copy() {
        return new StateChangePortal(this.type);
    }
}