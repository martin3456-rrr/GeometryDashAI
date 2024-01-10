package com.dataStructure;

import com.Component.Sprite;
import com.Component.Spritesheet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssertPool {
    static Map<String, Sprite> sprites = new HashMap<>();
    static Map<String, Spritesheet> spritesheets = new HashMap<>();
    public static boolean hasSprite(String pictureFile)
    {
        File tmp = new File(pictureFile);
        return AssertPool.sprites.containsKey(tmp.getAbsolutePath());
    }
    public static boolean hasSpritedsheet(String pictureFile)
    {
        File tmp = new File(pictureFile);
        return AssertPool.spritesheets.containsKey(tmp.getAbsolutePath());
    }
    public static Sprite getSprite(String pictureFile)
    {
        File file = new File(pictureFile);
        if(AssertPool.hasSprite(file.getAbsolutePath()))
        {
            return AssertPool.sprites.get(file.getAbsolutePath());
        }
        else
        {
            Sprite sprite = new Sprite(pictureFile);
            AssertPool.addSprite(pictureFile,sprite);
            return AssertPool.sprites.get(file.getAbsolutePath());
        }
    }

    public static Spritesheet getSpritesheet(String pictureFile)
    {
        File file = new File(pictureFile);
        if(AssertPool.hasSpritedsheet(file.getAbsolutePath()))
        {
            return AssertPool.spritesheets.get(file.getAbsolutePath());
        }
        else
        {
            System.out.println("Spritesheet "+pictureFile+"does not exist");
            System.exit(-1);
        }
        return null;
    }

    public static void addSprite(String pictureFile,Sprite sprite)
    {
        File file = new File(pictureFile);
        if(!AssertPool.hasSprite(file.getAbsolutePath()))
        {
            AssertPool.sprites.put(file.getAbsolutePath(),sprite);
        }
        else
        {
            System.out.println("Asset pool already has asset: "+file.getAbsolutePath());
            System.exit(-1);
        }
    }
    public static void addSpritesheet(String pictureFile,int tileWidth,int tileHeight,
                                      int spacing,int columns,int size)
    {
        File file = new File(pictureFile);
        if(!AssertPool.hasSpritedsheet(file.getAbsolutePath()))
        {
            Spritesheet spritesheet = new Spritesheet(pictureFile,tileWidth,tileHeight,spacing,columns,size);
            AssertPool.spritesheets.put(file.getAbsolutePath(),spritesheet);
        }
    }
}
