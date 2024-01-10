package com.UI;

import com.Component.BoxBounds;
import com.Component.Sprite;
import com.Component.Spritesheet;
import com.Component.TriangleBounds;
import com.dataStructure.AssertPool;
import com.dataStructure.Transform;
import com.jade.Component;
import com.jade.GameObject;
import com.jade.Window;
import com.util.Constants;
import com.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainContainer extends Component {
    public Sprite containerBg;
    public List<GameObject> menuItems;
    public List<GameObject> tabs;
    public Map<GameObject,List<GameObject>> tabMaps;
    private GameObject hotTab = null;
    private GameObject hotButton = null;
    public MainContainer()
    {
        this.menuItems = new ArrayList<>();
        this.tabs = new ArrayList<>();
        this.tabMaps = new HashMap<>();
        this.containerBg = AssertPool.getSprite("assets/ui/ContainerBackground.png");
        init();
    }

    public void init() {
        Spritesheet tabSprites = AssertPool.getSpritesheet("assets/ui/tabs.png");

        for(int i = 0;i<tabSprites.sprite.size();i++)
        {
            Sprite currentTab = tabSprites.sprite.get(i);

            int x = Constants.TAB_OFFSET_X + (currentTab.column*Constants.TAB_WIDTH) + (currentTab.column * Constants.TAB_HORIZONTAL_SPACING);
            int y = Constants.TAB_OFFSET_Y;

            GameObject obj = new GameObject("Tab",new Transform(new Vector2(x,y)),10);
            obj.setUI(true);
            obj.setNonserializable();
            TabItem item = new TabItem(x,y,Constants.TILE_WIDTH,Constants.TILE_HEIGHT,currentTab,this);
            obj.addComponent(item);

            this.tabs.add(obj);
            this.tabMaps.put(obj,new ArrayList<>());
            Window.getWindow().getCurrentScene().addGameObject(obj);
        }
        this.hotTab = this.tabs.get(0);
        this.hotTab.getComponent(TabItem.class).isSelected = true;


        addTabObject();

    }
    private void addTabObject()
    {
        Spritesheet groundSprites = AssertPool.getSpritesheet("assets/groundSprites.png");
        Spritesheet buttonSprites = AssertPool.getSpritesheet("assets/ui/buttonSprites.png");
        Spritesheet spikeSprites = AssertPool.getSpritesheet("assets/spikes.png");
        Spritesheet bigSprites = AssertPool.getSpritesheet("assets/bigSprites.png");
        Spritesheet smallBlocks = AssertPool.getSpritesheet("assets/smallBlocks.png");
        Spritesheet portalSprites = AssertPool.getSpritesheet("assets/portal.png");

        for (int i = 0; i < groundSprites.sprite.size(); i++)
        {
            Sprite currentSprite  = groundSprites.sprite.get(i);
            int x = Constants.BUTTON_OFFSET_X + (currentSprite.column*Constants.BUTTON_WIDTH) + (currentSprite.column * Constants.BUTTON_SPACING_HZ);
            int y = Constants.BUTTON_OFFSET_Y + (currentSprite.row * Constants.BUTTON_HEIGHT) + (currentSprite.row * Constants.BUTTON_SPRACING_VT);

            //Add first tab container objs
            GameObject obj = new GameObject("Gen", new Transform(new Vector2(x,y)),10);
            obj.setUI(true);
            obj.setNonserializable();
            obj.addComponent(currentSprite.copy());
            MenuItem menuItem = new MenuItem(x,y,Constants.BUTTON_WIDTH,Constants.BUTTON_HEIGHT,buttonSprites.sprite.get(0),buttonSprites.sprite.get(1),this);
            obj.addComponent(menuItem);
            obj.addComponent(new BoxBounds(Constants.TILE_WIDTH,Constants.TILE_HEIGHT));
            this.tabMaps.get(this.tabs.get(0)).add(obj);

            //Add second tab container objs
            if(i<smallBlocks.sprite.size())
            {
                obj = new GameObject("Gen",new Transform(new Vector2(x,y)),10);
                obj.setUI(true);
                obj.setNonserializable();
                menuItem = menuItem.copy();
                obj.addComponent(smallBlocks.sprite.get(i));
                obj.addComponent(menuItem);

                if(i==0)
                {
                    obj.addComponent(new BoxBounds(Constants.TILE_WIDTH,16));
                }
                this.tabMaps.get(tabs.get(1)).add(obj);
            }

            //Add fourth tab container objs
            if(i < spikeSprites.sprite.size())
            {
                obj = new GameObject("Gen",new Transform(new Vector2(x,y)),10);
                obj.setNonserializable();
                obj.setUI(true);
                menuItem = menuItem.copy();
                obj.addComponent(spikeSprites.sprite.get(i));
                obj.addComponent(menuItem);

                // TODO:: Add triangleBounds component here
                obj.addComponent(new TriangleBounds(42,42));
                this.tabMaps.get(this.tabs.get(3)).add(obj);
            }

            // Add fifth tab container objs
            if(i == 0)
            {
                obj = new GameObject("Gen",new Transform(new Vector2(x,y)),10);
                obj.setUI(true);
                obj.setNonserializable();
                menuItem = menuItem.copy();
                obj.addComponent(menuItem);
                obj.addComponent(bigSprites.sprite.get(i));

                obj.addComponent(new BoxBounds(Constants.TILE_WIDTH*2,56));
                this.tabMaps.get(tabs.get(4)).add(obj);
            }

            //Add sixth tab container objs
            if(i < portalSprites.sprite.size())
            {
                obj = new GameObject("Gen",new Transform(new Vector2(x,y)),10);
                obj.setUI(true);
                obj.setNonserializable();
                menuItem = menuItem.copy();
                obj.addComponent(menuItem);
                obj.addComponent(portalSprites.sprite.get(i));

                obj.addComponent(new BoxBounds(44,85));

                // TODO:: Create portalComponent here

                this.tabMaps.get(tabs.get(5)).add(obj);
            }

        }
    }
    @Override
    public void start()
    {
        for(GameObject g: tabs)
        {
            for (GameObject g2 : tabMaps.get(g))
            {
                for(Component c :g2.getAllComponents())
                {
                    c.start();
                }
            }

        }
    }
    @Override
    public void update(double dt)
    {
        for(GameObject g : this.tabMaps.get(hotTab))
        {
            g.update(dt);

            MenuItem menuItem = g.getComponent(MenuItem.class);
            if(g!=hotButton && menuItem.isSelected)
            {
                menuItem.isSelected = false;
            }
        }

        for(GameObject g : this.tabs)
        {
            TabItem tabItem = g.getComponent(TabItem.class);
            if(g!=hotTab && tabItem.isSelected)
            {
                tabItem.isSelected = false;
            }
        }
    }
    @Override
    public Component copy() {
        return null;
    }
    @Override
    public void draw(Graphics2D g2)
    {
        g2.drawImage(this.containerBg.image,0,Constants.CONTAINER_OFFSET_Y,
                this.containerBg.width,this.containerBg.height,null);
        for(GameObject g : this.tabMaps.get(hotTab))
        {
            g.draw(g2);
        }
    }

    @Override
    public String serialize(int tabSize) {
        return "";
    }
    public void setHotButton(GameObject obj)
    {
        this.hotButton = obj;
    }
    public void setHotTab(GameObject obj)
    {
        this.hotTab = obj;
    }
}
