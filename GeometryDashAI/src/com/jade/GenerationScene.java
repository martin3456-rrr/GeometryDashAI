package com.jade;

import com.Component.Sprite;
import com.Generator.GeneticLevelGenerator;
import com.Generator.LevelChromosome;
import com.Generator.LevelGenerationConfig;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class GenerationScene extends Scene {
    private volatile boolean generationComplete = false;
    private List<String> logs;

    public GenerationScene(String name) {
        super.Scene(name);
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
        Log.clear();
        Thread generationThread = new Thread(() -> {
            LevelGenerationConfig config = new LevelGenerationConfig(
                    Window.selectedDifficulty,
                    Constants.DEFAULT_LEVEL_LENGTH * 2,
                    Set.of(),
                    1.0,
                    Window.selectedModelType
            );
            GeneticLevelGenerator generator = new GeneticLevelGenerator();
            Window.generatedChromosome = generator.generateBestLevel(config);
            generationComplete = true;
        });
        generationThread.start();
    }

    @Override
    public void update(double dt) {
        logs = Log.getMessages();
        if (generationComplete) {
            if (Window.generatedChromosome != null) {
                Window.getWindow().changeScene(2);
            } else {
                Window.getWindow().changeScene(0);
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        renderer.render(g2);

        g2.setColor(Color.WHITE);
        int y = 30;
        int x = 20;

        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("Generating Level...", x, y);
        y += 40;

        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        if (logs != null) {
            int start = Math.max(0, logs.size() - 40);
            for (int i = start; i < logs.size(); i++) {
                g2.drawString(logs.get(i), x, y);
                y += 15;
            }
        }
    }
}