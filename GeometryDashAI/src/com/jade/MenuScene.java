package com.jade;

import com.Component.Ground;
import com.Component.ParallaxBackgrounds;
import com.Component.Sprite;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;


import java.awt.*;
import java.awt.event.MouseEvent;

public class MenuScene extends Scene {
    private Rectangle startGameButton;
    private Rectangle exitButton;
    private ML mouseListener;

    public MenuScene(String name) {
        super.Scene(name);
        camera = new Camera(new Vector2(0,0));
        renderer = new Renderer(camera);
        mouseListener = new ML();
        Window.getWindow().addMouseListener(mouseListener);
        Window.getWindow().addMouseMotionListener(mouseListener);

    }
    @Override
    public void init() {
        try {
            Sprite bgSprite = AssertPool.getSprite("assets/backgrounds/menu_bg.png");
            if (bgSprite != null) {
                GameObject menuBackground = new GameObject("MenuBackground", new Transform(new Vector2(0, 0)), -1);
                menuBackground.transform.scale = new Vector2(Constants.SCREEN_WIDTH / (float)bgSprite.width,
                        Constants.SCREEN_HEIGHT / (float)bgSprite.height);
                menuBackground.addComponent(bgSprite);
                addGameObject(menuBackground);
            } else {
                System.err.println("Nie udało się załadować sprite'a tła menu!");
            }
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas ładowania sprite'a tła menu: " + e.getMessage());
            e.printStackTrace();
        }
        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = Constants.SCREEN_WIDTH / 2 - buttonWidth / 2;
        int startY = 250;
        int spacing = 70;

        startGameButton = new Rectangle(centerX, startY, buttonWidth, buttonHeight);
        exitButton = new Rectangle(centerX, startY + 2 * spacing, buttonWidth, buttonHeight);
    }

    @Override
    public void update(double dt) {
        for (GameObject go : gameObject) {
            go.update(dt);
        }
        Vector2 mousePos = new Vector2(mouseListener.x, mouseListener.y);

        if (startGameButton != null && startGameButton.contains(mousePos.x, mousePos.y)) {
            if ((mouseListener.mousePressed && mouseListener.mouseButton == MouseEvent.BUTTON1)) {
                Window.selectedMode = Window.GameMode.ORIGINAL_LEVEL;
                Window.levelToLoad = "stereo_madness";
                System.out.println("Starting original level: " + Window.levelToLoad);
                Window.getWindow().changeScene(1);
                mouseListener.mousePressed = false;
            }
        } else if (exitButton != null && exitButton.contains(mousePos.x, mousePos.y)) {
            if (mouseListener.mousePressed && mouseListener.mouseButton == MouseEvent.BUTTON1) {
                Window.getWindow().close();
            }
        }
    }
    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(50, 50, 100));
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        renderer.render(g2);

        drawButton(g2, startGameButton, "Start Game");
        drawButton(g2, exitButton, "Exit");
    }
    private void drawButton(Graphics2D g2, Rectangle buttonRect, String text) {
        boolean hovered = buttonRect.contains(mouseListener.x, mouseListener.y);

        if (hovered) {
            g2.setColor(new Color(120, 120, 180));
        } else {
            g2.setColor(new Color(80, 80, 150));
        }
        g2.fillRect(buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);

        g2.setColor(Color.WHITE);
        g2.drawRect(buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        int textWidth = g2.getFontMetrics().stringWidth(text);
        int textHeight = g2.getFontMetrics().getHeight();
        g2.drawString(text, buttonRect.x + buttonRect.width / 2 - textWidth / 2, buttonRect.y + buttonRect.height / 2 + textHeight / 4);
    }
}
