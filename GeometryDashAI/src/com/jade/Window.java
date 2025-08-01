package com.jade;

import com.Generator.GenerationModelType;
import com.Generator.LevelChromosome;
import com.manager.Difficulty;
import com.util.Constants;
import com.util.Time;

import javax.swing.JFrame;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.Generator.PatternLibrary;

public class Window extends JFrame implements Runnable {
    public enum GameMode { AI_GENERATED, ORIGINAL_LEVEL }
    public static Difficulty selectedDifficulty = Difficulty.MEDIUM;
    public static GenerationModelType selectedModelType = GenerationModelType.MARKOV;
    public ML MouseListener;
    public KL keyLister;
    public boolean isInEditor = false;
    private static Window window = null;
    public boolean isRunning = true;
    private Scene currentScene = null;
    private Image doubleBufferImage = null;
    private Graphics doubleBufferGraphics = null;
    public static GameMode selectedMode = GameMode.AI_GENERATED;
    public static List<String> availableLevels = new ArrayList<>(PatternLibrary.getOriginalLevels().keySet());
    public static int selectedLevelIndex = 0; 
    public static String levelToLoad = availableLevels.get(selectedLevelIndex);
    public static LevelChromosome generatedChromosome = null;

    public Window()
    {
        this.MouseListener = new ML();
        this.keyLister = new KL();
        this.setSize(Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
        this.setTitle(Constants.SCREEN_TITLE);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(keyLister);
        this.addMouseListener(MouseListener);
        this.addMouseMotionListener(MouseListener);
        this.setLocationRelativeTo(null);
    }
    public void init()
    {
        changeScene(0);
    }
    public Scene getCurrentScene()
    {
        return currentScene;
    }
    public void changeScene(int scene)
    {
        switch(scene)
        {
            case 0:
                currentScene = new MenuScene("Menu");
                currentScene.init();
                break;
            case 1:
                currentScene = new OptionsScene("Options");
                currentScene.init();
                break;
            case 2:
                currentScene = new LevelScene("Level");
                currentScene.init();
                break;
            case 3:
                currentScene = new GenerationScene("Generating...");
                currentScene.init();
                break;
            default:
                System.out.println("Do not know what this scene is.");
                currentScene = null;
                break;
        }
    }
    public static Window getWindow()
    {
        if(Window.window == null)
        {
            Window.window = new Window();
        }
        return Window.window;
    }
    public static Scene getScene()
    {
        return getWindow().getCurrentScene();
    }
    public static ML mouseListener()
    {
        return getWindow().MouseListener;
    }
    public static KL keyListener()
    {
        return getWindow().keyLister;
    }
    public void update(double dt)
    {
        currentScene.update(dt);
        draw(getGraphics());
    }
    public void draw(Graphics g)
    {
        if(doubleBufferImage == null)
        {
            doubleBufferImage = createImage(getWidth(), getHeight());
            doubleBufferGraphics = doubleBufferImage.getGraphics();
        }
        renderOffscreen(doubleBufferGraphics);

        g.drawImage(doubleBufferImage,0,0,getWidth(),getHeight(),null);
    }
    public void renderOffscreen(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        currentScene.draw(g2);
    }
    @Override
    public void run()
    {
        double lastFrameTime = 0.0;
        try{
            while(isRunning) {
                double time = Time.getTime();
                double deltaTime = time - lastFrameTime;
                lastFrameTime = time;
                update(deltaTime);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void cycleDifficulty() {
        Difficulty[] difficulties = Difficulty.values();
        int currentIndex = selectedDifficulty.ordinal();
        selectedDifficulty = difficulties[(currentIndex + 1) % difficulties.length];
        System.out.println("Selected difficulty: " + selectedDifficulty);
    }

    public static void cycleModelType() {
        GenerationModelType[] models = GenerationModelType.values();
        int currentIndex = selectedModelType.ordinal();
        selectedModelType = models[(currentIndex + 1) % models.length];
        System.out.println("Selected model type: " + selectedModelType);
    }
    public static void nextLevel() {
        selectedLevelIndex = (selectedLevelIndex + 1) % availableLevels.size();
        levelToLoad = availableLevels.get(selectedLevelIndex);
    }

    public static void previousLevel() {
        selectedLevelIndex = (selectedLevelIndex - 1 + availableLevels.size()) % availableLevels.size();
        levelToLoad = availableLevels.get(selectedLevelIndex);
    }
    public void close() {
        System.exit(0);
    }


}