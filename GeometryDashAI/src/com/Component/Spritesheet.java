package com.Component;

import com.dataStructure.AssertPool;

import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    public List<Sprite> sprite;
    public int tileWidth;
    public int tileHeight;
    public int spacing;
    public Spritesheet(String pictureFile,int tileWidth,int tileHeight,int spacing,int columns,int size)
    {
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.spacing = spacing;

        Sprite parent = AssertPool.getSprite(pictureFile);
        sprite = new ArrayList<>();
        int row = 0;
        int count = 0;
        while(count<size)
        {
            for(int column = 0; column < columns; column++)
            {
                int imgX = (column*tileWidth)+(column*spacing);
                int imgY = (row*tileHeight)+(row*spacing);

                sprite.add(new Sprite(parent.image.getSubimage(imgX,imgY,tileWidth,tileHeight)));
                count++;
                if(count>size-1)
                {
                    break;
                }
            }
            row++;
        }
    }
}
