package com.Component;

import com.dataStructure.AssertPool;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.List;

public class Spritesheet {
    public List<Sprite> sprite;
    public int tileWidth;
    public int tileHeight;
    public int spacing;

    public Spritesheet(String pictureFile, int tileWidth, int tileHeight, int spacing, int columns, int size) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.spacing = spacing;
        this.sprite = new ArrayList<>();

        Sprite parent = AssertPool.getSprite(pictureFile);

        if (parent == null || parent.image == null) {
            System.err.println("Error: Failed to load image for spritesheet: " + pictureFile);
            return;
        }

        BufferedImage parentImage = parent.image;
        int parentWidth = parentImage.getWidth();
        int parentHeight = parentImage.getHeight();

        int count = 0;
        int row = 0;

        System.out.println("Loading Spritesheet: " + pictureFile + " [" + parentWidth + "x" + parentHeight + "]");

        while (count < size) {
            for (int col = 0; col < columns; col++) {
                if (count >= size) break;

                int imgX = col * (tileWidth + spacing);
                int imgY = row * (tileHeight + spacing);

                if (imgX + tileWidth > parentWidth || imgY + tileHeight > parentHeight) {
                    System.err.println("Skipping tile out of bounds at [row=" + row + ", col=" + col + "] -> (" + imgX + "," + imgY + ")");
                    count++;
                    continue;
                }

                try {
                    BufferedImage subImage = parentImage.getSubimage(imgX, imgY, tileWidth, tileHeight);
                    sprite.add(new Sprite(subImage, row, col, count, pictureFile));
                } catch (RasterFormatException e) {
                    System.err.println("RasterFormatException for tile [row=" + row + ", col=" + col + "] at (" + imgX + "," + imgY + ")");
                }

                count++;
            }
            if (count >= size) {
                break;
            }


            row++;

            if ((row * (tileHeight + spacing)) >= parentHeight) {
                if (count < size) {
                    System.err.println("Warning: Loaded only " + count + "/" + size + " sprites. Next row [" + row + "] would exceed image height (" + parentHeight + "). Stopping load for: " + pictureFile);
                }
                break;
            }

        }

        if (count < size) {
            System.err.println("Final Warning: Only " + count + " out of " + size + " sprites loaded from " + pictureFile);
        } else {
            System.out.println("Spritesheet loaded successfully: " + count + " sprites from " + pictureFile);
        }
    }
}