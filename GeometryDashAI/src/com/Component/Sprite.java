package com.Component;

import com.dataStructure.AssertPool;
import com.jade.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

public class Sprite extends Component {
    public BufferedImage image;
    public String pictureFile;

    public int width,height;
    public boolean isSubsprite = false;
    public int row,column,index;
    public Sprite(String pictureFile)
    {
        this.pictureFile=pictureFile;
        try {
            File file = new File(pictureFile);
            if(AssertPool.hasSprite(pictureFile))
            {
                throw new Exception("Asset already exists: " + pictureFile);
            }
            this.image = ImageIO.read(file);
            this.width= image.getWidth();
            this.height = image.getHeight();
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public Sprite(BufferedImage image,String pictureFile)
    {
        this.image=image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pictureFile = pictureFile;
    }
    public Sprite(BufferedImage image,int row,int column,int index,String pictureFile)
    {
        this.image=image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.row=row;
        this.column=column;
        this.index=index;
        this.isSubsprite = true;
        this.pictureFile = pictureFile;
    }
    @Override
    public void draw(Graphics2D g2)
    {
        AffineTransform transform = new AffineTransform();
        transform.setToIdentity();
        transform.translate(gameObject.transform.position.x,gameObject.transform.position.y);
        transform.rotate(Math.toRadians(gameObject.transform.rotation),
                width*gameObject.transform.scale.x/2.0,height*gameObject.transform.scale.y/2.0);
        transform.scale(gameObject.transform.scale.x,gameObject.transform.scale.y);
        g2.drawImage(image,transform,null);
    }
    @Override
    public Component copy() {
        if(!isSubsprite)
        {
            return new Sprite(this.image,pictureFile);
        }
        else
        {
            return new Sprite(this.image,this.row,this.column,this.index,pictureFile);
        }
    }
}
