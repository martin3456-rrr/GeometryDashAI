package com.Component;

import com.jade.Component;
import com.jade.Window;

import java.awt.event.MouseEvent;

public class CameraControls extends Component {
    private float prevMx,prevMy;
    public CameraControls()
    {
      prevMx = 0.0f;
      prevMy = 0.0f;
    }
    @Override
    public void update(double dt)
    {
        if(Window.getWindow().MouseListener.mousePressed &&
                Window.getWindow().MouseListener.mouseButton == MouseEvent.BUTTON2)
        {
            float dx = (Window.getWindow().MouseListener.x + Window.getWindow().MouseListener.dx - prevMx);
            float dy = (Window.getWindow().MouseListener.y + Window.getWindow().MouseListener.dy - prevMy);

            Window.getWindow().getCurrentScene().camera.position.x -= dx;
            Window.getWindow().getCurrentScene().camera.position.y -= dy;
        }
        prevMx = Window.getWindow().MouseListener.x + Window.getWindow().MouseListener.dx;
        prevMy = Window.getWindow().MouseListener.y + Window.getWindow().MouseListener.dy;
    }
}
