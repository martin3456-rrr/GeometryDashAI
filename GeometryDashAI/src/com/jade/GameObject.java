package com.jade;

import com.dataStructure.Transform;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
     private List<Component> components;
     private String name;
     public Transform transform;
     public boolean serializable = true;
     public int zIndex;
     public boolean isUI = false;
     private Scene scene;
     public GameObject(String name, Transform transform,int zIndex)
     {
         this.name=name;
         this.transform=transform;
         this.components = new ArrayList<>();
         this.zIndex = zIndex;
     }
     public<T extends Component> T getComponent(Class<T> componentClass)
     {
       for(Component c : components)
       {
           if(componentClass.isAssignableFrom(c.getClass()))
           {
               try
               {
                   return componentClass.cast(c);
               }catch(ClassCastException e)
               {
                   e.printStackTrace();
                   System.exit(-1);
               }
           }
       }
       return null;
     }
     public <T extends Component> void removeComponent(Class<T> componentClass)
     {
        for(Component c : components)
        {
            if(componentClass.isAssignableFrom(c.getClass()))
            {
                components.remove(c);
                return;
            }
        }
     }
     public List<Component> getAllComponents()
     {
            return this.components;
     }
     public void addComponent(Component c)
     {
         components.add(c);
         c.gameObject=this;
     }

     public GameObject copy()
     {
         GameObject newGameObject = new GameObject("Generated",transform.copy(),this.zIndex);
         for(Component c : components)
         {
             Component copy = c.copy();
             if(copy!=null)
             {
                 newGameObject.addComponent(copy);
             }

         }
         return newGameObject;
     }
     public void update(double dt)
     {
         for(Component c : components)
         {
             c.update(dt);
         }
     }
     public void setNonserializable()
     {
         serializable = false;
     }
     public void draw(Graphics2D g2)
     {
         for(Component c : components)
         {
             c.draw(g2);
         }
     }
     public void setUI(boolean val)
     {
         this.isUI = val;
     }
     public String getName() {
        return this.name;
     }
}
