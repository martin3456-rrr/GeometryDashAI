package com.jade;

import com.manager.DifficultyManager;
import com.util.Constants;
import com.util.Time;

import javax.swing.JFrame;
import java.awt.*;

public class Window extends JFrame implements Runnable {
    public ML MouseListener;
    public KL keyLister;
    public boolean isInEditor = false;
    private static Window window = null;
    public boolean isRunning = true;
    private Scene currentScene = null;
    private Image doubleBufferImage = null;
    private Graphics doubleBufferGraphics = null;
    private DifficultyManager difficultyManager;
    public Window()
    {
        this.MouseListener = new ML();
        this.keyLister = new KL();
        this.difficultyManager = DifficultyManager.getInstance();
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
                currentScene = new LevelScene("Level","Stereo Madness");
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
    public DifficultyManager getDifficultyManager() {
        return this.difficultyManager;
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
    public void close() {
        System.exit(0);
    }
}