package com.lukepop.game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.Collections;

import com.lukepop.entity.Cloud;
import com.lukepop.entity.Entity;
import com.lukepop.game.GameApplet.Layout;
import com.lukepop.island.Clock;
import com.lukepop.island.Images;
import com.lukepop.island.Island;
import com.lukepop.island.MenuHandler;
import com.lukepop.supernatural.Supernatural;

public class MainComponent extends Canvas implements Runnable, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener , FocusListener
{
	 //Start xCenter = 300, Start yCenter = 196
	 //BOUNDARIES: 
	 //              (300, 388)
	 //		-------------------------------
	 //		-                             -
	 //		-                             -
//(720,196) -        (300, 196)           - (-90, 196)                            -
	 //		-                             -
	 //		-                             -
	 //		-                             -
	 //		-------------------------------
	 //         	 (300, -50)
	public static Layout layout;
	public static final double ZOOM_CAP_ISLAND_HIGH = 12;
	public static final double ZOOM_CAP_ISLAND_LOW = 0.3;
	public static final double ZOOM_CAP_ENTITY_HIGH = 8;
	public static final double ZOOM_CAP_ENTITY_LOW = 0.2;
	
	public static final int TICKS_PER_SECOND = 30;
	private static final int MAX_TICKS_PER_FRAME = 10;
	private static final long serialVersionUID = 1L;
	public static int gameSpeed; //0 = paused 1 = regular 2 = faster, 3 = fast 4 = super fast
	 
    private boolean running;
	public int WIDTH, HEIGHT;
	private VolatileImage image;
	private Thread thread;
	private int tickCount, displayTickCount;
	private int frames;
	private boolean paused, pauseScreenDrawn;
	public Rectangle PAUSE_SCREEN = null;
    private int xCenter, yCenter;
	private int gameTime;

	//Rotation/zoom/geometrical elements:
    private int xMouse = -1, yMouse;
    private boolean KEY_ACTIONS[]; //0: up, 1: right 2: down 3: left
	public double xRot, xRotA;
	public double zoomFactorIsland = 1.5;
	public double zoomFactorEntity = 1;

	private boolean scrolling = false, focusLost = false;
	private double xScrollStart;
	private int xTranslateAdd, yTranslateAdd;

	//Images and Music:
	public Island island;
	public Images images;
	public boolean musicOn = true;
	
	public Supernatural powerHandler;
	public boolean superMode;
	public Rectangle selectBox;
	public Clock clock;
	
	//Factor to shake the island, handled by a Supernatural object.
	public int shakeFactor;
	 
	//Static elements:
	private Rectangle menuBox, menuTag; 
	public String menuStatus = "None"; //None, Open, Closed
	private int menuXLocation = 0;
	public int illigalFlagTimer = 0; //This is just for spending pts when you don't have em.
	 
    public MainComponent(Layout inlayout, int width, int height)
    {   
        this.WIDTH = width; 
        this.HEIGHT = height; 
        layout = inlayout;
        images = layout.images;
        setSize(width * 2, height * 2);
        addMouseMotionListener(this); addMouseListener(this); addMouseWheelListener(this); 
        addFocusListener(this); addKeyListener(this);
        KEY_ACTIONS = new boolean[4];
        //This boolean is the biggest difference between classic and sandbox games. True would
        //let the user have unlimited god powers
    }
    
    //Put all re-settable properties in this constructor, not the "actual"
    //constructor, because this is called every card switch.
    private void init()
    {
    	try
    	{
    		menuBox = new Rectangle(40, HEIGHT);
    		menuTag = new Rectangle(5, 30);
    		selectBox = new Rectangle(35, 35);
    		PAUSE_SCREEN = new Rectangle(40, 0, WIDTH * 2, HEIGHT * 2);
    	}
    	catch(Exception e) {e.printStackTrace();} 
    	
    	island = new Island(this, images);
    	gameTime = 0;
    	gameSpeed = 1;
        xCenter = WIDTH / 2;
        yCenter = HEIGHT * 55 / 100;
        clock = new Clock();
        powerHandler = new Supernatural(this);   
    }
   
    public void unpause()
    {
        if(thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
        paused = false;
    }

    public void pause()
    {
        paused = true;
    }

    public void paint(Graphics g){}

    public void update(Graphics g){}

    public void start(boolean superMode)
    {
        thread = new Thread(this);
        thread.start();
        this.superMode = superMode;
    }

    public void stop()
    {
        running = false;
        try
        {
            if (thread != null) thread.join();
        }
        catch (InterruptedException e){}
    }
    //A one time method, but contains the game loop inside.
	public void run() 
	{
		init();
        float lastTime = (System.nanoTime() / 1000000) / 1000.0f; //milliseconds
        running = true;

        double msPerTick = 1.0 / TICKS_PER_SECOND;
        double ticks = 0;
        
        while(running)
        {
            synchronized(this)
            {
                float now = (System.nanoTime() / 1000000) / 1000.0f;
                int frameTicks = 0; 
                
                if(gameSpeed > 0)
                {
                	if(paused)
                	{
                		this.unpause();
                		pauseScreenDrawn = false;
                	}
                	//If game speed is just one, do a slow 30 ticks per second render.
                    if(gameSpeed == 1) 
                    {
                    	while (now - lastTime > msPerTick) 
    	                {
    	                    if(!paused && frameTicks++ < MAX_TICKS_PER_FRAME)
    	                    {
    	                    	tick();   //A lot less than one tick per frame.
    	                    	userInputTick();
    	                    }
    	                    lastTime += msPerTick;
    	                }                
                    } 
                    else //If the speed is 1 - 3
		            {
		                while (ticks < 4)
		                {
		                    if(!paused && frameTicks++ < MAX_TICKS_PER_FRAME)	
		                    	tick();	                  
		                    ////////////////////////////////////////////////////
		                    if(gameSpeed == 2) //One tick per frame  	
		                    	ticks += 4;
		                    else if(gameSpeed == 3) //Two ticks per frame
		                    	ticks += 2;
		                    else              //Four ticks per frame
		                    	ticks++;
		                    ////////////////////////////////////////////////////
		                }    
		                ticks = 0;
		                //Registers so the user input (rotation, scroll, etc) stays the same speed.
		                while (now - lastTime > msPerTick)
		                {
		                	userInputTick();
		                	lastTime += msPerTick;
		                }
		            }
                }
                
                else if(gameSpeed == 0)
                	this.pause();
              
                if(!paused)
                    render((now - lastTime) / msPerTick);  
                else
                	pauseRender();
            }
            try
            {
                Thread.sleep(paused ? 200 : 4);
            }
            catch (InterruptedException e) {e.printStackTrace();}
        }
	}
	
	//This ticks controls user input, like scrolling, rotating, and zooming.
	//Fixed at a slower rate, to mitigate the bug in which when the speed is increased, 
	//so is the scrolling.
	private void userInputTick()
	{
		xRot += xRotA;
        xRotA *= 0.7;
        //To compute left click scrolling, we do a displacement calculation.
        //Record the initial x value, and keep listening for new x values, 
        //compute an x displacement, and adjust the rotation to that displacement.
        if (scrolling)
        {
            double xd = xMouse - xScrollStart;
            xRotA -= xd / 5000.0; 
        }
        else if(!focusLost)
        {	
        	if(KEY_ACTIONS[0]) { yTranslateAdd = 3; }
        	if(KEY_ACTIONS[1]) { xTranslateAdd = -3; }
        	if(KEY_ACTIONS[2]) { yTranslateAdd = -3; }
        	if(KEY_ACTIONS[3]) { xTranslateAdd = 3;  }
        }
        island.rot = xRot;
	}
	
	//Called x times per frame based off game speed.
	//This tick affects FPS directly, and this dictates game speed (island ticks).
	private void tick()
    {
        tickCount++;
        if (tickCount % TICKS_PER_SECOND == 0)  
        {
        	displayTickCount = frames;
        	frames = 0;        
        }
        gameTime++;
        island.tick();
    }
	//Main goal is to call render game, this method handles the FPS mainly.
	//Called once per frame
	public void render(double alpha)
	{
		frames++;
        BufferStrategy bs = getBufferStrategy();
        if(bs == null)
        {
            createBufferStrategy(2);
            bs = getBufferStrategy();
        }

        if(image == null)
            image = createVolatileImage(WIDTH, HEIGHT);

        if(bs != null)
        {
            Graphics2D g = image.createGraphics();
            Graphics2D clone = (Graphics2D) g.create();
            renderGame(g, clone, alpha);
            g.dispose();

            Graphics gg = bs.getDrawGraphics();
            gg.drawImage(image, 0, 0, WIDTH * 2, HEIGHT * 2, 0, 0, WIDTH, HEIGHT, null);
            gg.dispose();
            bs.show();
        }
	}  
	//A special render method only called (looped) when the game is paused.
	public void pauseRender()
	{
    	 BufferStrategy bs = getBufferStrategy();
    	
         if(bs == null)
         {
             createBufferStrategy(2);
             bs = getBufferStrategy();
         }

         if(image == null)
             image = createVolatileImage(WIDTH, HEIGHT);

         if(bs != null)
         {
        	 Graphics2D g = image.createGraphics();
        	 
        	 
        	 if(!pauseScreenDrawn)
        	 {
	     		 g.setColor(new Color(80, 0, 0, 128));
	        	 g.fill(PAUSE_SCREEN);
	             pauseScreenDrawn = true;            
        	 }
        	 
             g.setColor(Color.WHITE);
             g.setFont(new Font("Consolas", Font.PLAIN, 10));
             g.drawString("PAUSED", WIDTH / 2, 35);
             //Keep the side bar running for god powers.
             this.renderStatic(g);
            
             g.dispose();
             
             Graphics gg = bs.getDrawGraphics();
             gg.drawImage(image, 0, 0, WIDTH * 2, HEIGHT * 2, 0, 0, WIDTH, HEIGHT, null);
             gg.dispose();
             bs.show();
         }
	}
	//Main render method which draws the game.
	public void renderGame(Graphics2D g, Graphics2D clone, double alpha)
	{
		if(!musicOn && layout != null)
			layout.sequencer.stop();
		else if(musicOn && layout != null)
			layout.sequencer.start();

        //Set the out of bounds universe
        g.setColor(new Color(0x3079d4)); 
        g.fillRect(0, 0, WIDTH, HEIGHT);

        double rot = xRot + xRotA * alpha;
        double sin = Math.sin(rot); 
        double cos = Math.cos(rot); 

        //This for/update sequence will update the x/y locations to correspond with
        //the island's rotation (alpha).
        for (int i = 0; i < island.entities.size(); i++)
        {
            Entity e = island.entities.get(i);
            e.updatePos(sin, cos, alpha);  
        }
        
        //Sort the existing entities by their depth.
        Collections.sort(island.entities);
        
    	//Clouds get depth priority over other entities because they
    	//need to look like they are floating in the air.
        for(int i = 0; i < island.entities.size(); i++)
        {
        	 Entity e = island.entities.get(i);
        	 if(e instanceof Cloud)
        	 {
        		 //NOTE: The index is not decremented after this list removal because
        		 //we are instantly re-adding it, a decrement causes an infinite loop.
        		 island.entities.remove(i);
        		 island.entities.add(e);
        	 }
        }
      
        //Collections.sort(island.entities.subList(island.entities.size() - supernaturalSize, island.entities.size()));
        //Tick supernatural events, like the ground shaking for example.
        powerHandler.tick();
        
        //NOTE**: After every transform we must we translate the image to
        //the center of the map!! (Unless image has no rotation, like title, then we just transform)
        AffineTransform af = g.getTransform();
        //Translate to the proper center so we can operate math/trig,
        g.translate(xCenter += (xTranslateAdd + shakeFactor), yCenter += (yTranslateAdd));
        																							
        //This scale moves the image forward, so its bigger, default 1.5
        g.scale(zoomFactorIsland, zoomFactorIsland);
        //This scale shifts the island so its laying down instead of facing up
        g.scale(1, 0.5);      
        //Handles rotation
        g.rotate(-rot);    
        
        //**The island must be translated -128, -128 total, but we are
        //translating the ocean first (-248, -280)
        //This translate shifts the island/ocean to the center of the screen so we can view it.
         
        g.translate(-248, -280);
        g.drawImage(images.ocean, 0, 0, WIDTH, (int) (HEIGHT * 1.75), null);
        
        g.translate(120, 152);
        g.drawImage(images.island, 0, 0, null);        
        //Applies all previous translates, scales, and rotates and sets the transform. 
        g.setTransform(af);  
        
        //Retranslate to the center for our entities, entities are not transformed!
        g.translate(xCenter += xTranslateAdd, yCenter += yTranslateAdd);   
        //Entities need their own special zoom factor.
        g.scale(zoomFactorEntity, zoomFactorEntity);
        //Reset the translate.
        xTranslateAdd = 0; yTranslateAdd = 0;
        //Draw all entities on the island
        for (int i = 0; i < island.entities.size(); i++)
            island.entities.get(i).render(g, alpha);
        
        //Render static for every game render, but when were paused just render static!
        this.renderStatic(clone);
	}
    
	//The side bar needs it's own method because pausing the game still allows for god powers.
	public void renderStatic(Graphics2D clone)
	{
		 int seconds = gameTime / MainComponent.TICKS_PER_SECOND;
	     int minutes = seconds / 60;
	     int hours = minutes / 60;
	     seconds %= 60;
	     minutes %= 60;

	     String timeStr = "";
	     if (hours > 0)
	     {
	         timeStr += hours + ":";
	         if (minutes < 10) timeStr += "0";
	     }
	     timeStr += minutes + ":";
	     if (seconds < 10) timeStr += "0";
	     timeStr += seconds;
	     
        //Drawing the menu bar and its contents, along with the time and fps strings
        int moveMenu = 0;
        if(menuStatus.equals("Closed") && menuTag.getMinX() >= 5)
        	moveMenu = -5;
        
        else if(menuStatus.equals("Open") && menuTag.getMaxX() <= 40)
        	moveMenu = 5;
        
        //Menu bar and tag: 
        clone.setColor(new Color(222, 184, 135));
        menuBox.setLocation(menuXLocation += moveMenu, 0);
        clone.drawImage(images.menuBar, (int) menuBox.getX(), (int) menuBox.getY(), (int) menuBox.getWidth(), (int) menuBox.getHeight(), null);
        //Speed buttons:
        
        //If we mouse over slow down.
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 0 &&
        		xMouse <= 30 && yMouse >= 55 && yMouse <= 85)
        	clone.drawImage(images.speedButtons[3], (int) menuBox.getX() + 4, (int) menuBox.getY() + 30, 12, 12, null);
        else
        	clone.drawImage(images.speedButtons[1], (int) menuBox.getX() + 4, (int) menuBox.getY() + 30, 12, 12, null);
        
        //If we mouse over speed up.
		if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 30 &&
				xMouse <= 80 && yMouse >= 55 && yMouse <= 85)
			clone.drawImage(images.speedButtons[2], (int) menuBox.getX() + 24, (int) menuBox.getY() + 30, 12, 12, null);
		else
			clone.drawImage(images.speedButtons[0], (int) menuBox.getX() + 24, (int) menuBox.getY() + 30, 12, 12, null);
       
        //Time:
		clone.setColor(Color.WHITE);
		clone.setFont(new Font("Consolas", Font.PLAIN, 9));
	    clone.drawString(timeStr, (int) menuBox.getX() + 10, (int) menuBox.getY() + 8); 

        //Clock:
        clock.render(clone, images.clock, (int) menuBox.getX() + 11, gameSpeed);
        
        //Meteorite icon: 
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 5 &&
        		xMouse <= 80 && yMouse >= 85 && yMouse <= 145)
        	clone.drawImage(images.godPowerButtons[2], (int) menuBox.getX() + 1, (int) menuBox.getY() + 45, 36, 36, null);
        else
        	clone.drawImage(images.godPowerButtons[2], (int) menuBox.getX() + 4, (int) menuBox.getY() + 45, 30, 30, null);
        
        //Lightning icon:
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse >= 160 && yMouse <= 220)
        	clone.drawImage(images.godPowerButtons[1], (int) menuBox.getX() + 1, (int) menuBox.getY() + 80, 36, 36, null);
        else
        	clone.drawImage(images.godPowerButtons[1], (int) menuBox.getX() + 4, (int) menuBox.getY() + 80, 30, 30, null);
        
        //Rain Icon:
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse >= 230 && yMouse <= 290)
        	clone.drawImage(images.godPowerButtons[0], (int) menuBox.getX() - 2, (int) menuBox.getY() + 113, 36, 36, null);
        else
        	clone.drawImage(images.godPowerButtons[0], (int) menuBox.getX() + 2, (int) menuBox.getY() + 113, 30, 30, null);
        
        //Erase button:
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse <= (HEIGHT * 2) - 70 && yMouse >= (HEIGHT * 2) - 125)
        	clone.drawImage(images.godPowerButtons[3], (int) menuBox.getX() + 1, (int) menuBox.getMaxY() - 65, 36, 36, null);
        else
        	clone.drawImage(images.godPowerButtons[3], (int) menuBox.getX() + 4, (int) menuBox.getMaxY() - 65, 30, 30, null);
        
        //Volume buttons: 
        if((menuStatus.equals("Open") || menuStatus.equals("None")) && xMouse >= 15 &&
				xMouse <= 60 && yMouse >= (HEIGHT * 2) - 50 && yMouse <= (HEIGHT * 2) - 10)
		{
            if(musicOn)
            	clone.drawImage(images.volumeButtons[0], (int) menuBox.getX() + 7, (int) menuBox.getMaxY() - 27, 24, 24, null);
            else
            	clone.drawImage(images.volumeButtons[1], (int) menuBox.getX() + 7, (int) menuBox.getMaxY() - 27, 24, 24, null);
		}
        else
        {
            if(musicOn)
            	clone.drawImage(images.volumeButtons[0], (int) menuBox.getX() + 9, (int) menuBox.getMaxY() - 25, 20, 20, null);
            else
            	clone.drawImage(images.volumeButtons[1], (int) menuBox.getX() + 9, (int) menuBox.getMaxY() - 25, 20, 20, null);
        }
        ////////////////////////////////DRAW THE MENU-BOX-TAG//////////////////////////////////////
        if(xMouse >= 80 && xMouse <= 90 && yMouse <= 342 && yMouse >= 280 && (menuStatus.equals("Open") || menuStatus.equals("None")))
        	clone.setColor(new Color(20, 192, 252));
        else if(xMouse >= 0 && xMouse <= 9 && yMouse >= 280 && yMouse <= 342 && (menuStatus.equals("Closed") || menuStatus.equals("None")))
        	clone.setColor(new Color(20, 192, 252));
        else
        	clone.setColor(new Color(220, 209, 209));
        menuTag.setLocation((int) menuBox.getMaxX(), (int) menuBox.getCenterY() - 20) ;
        clone.fillOval((int) menuTag.getX(), (int) menuTag.getY(), (int) menuTag.getWidth(), (int) menuTag.getHeight());
        
        /////////////////////////////////////Select box code///////////////////////////////////////
        if(powerHandler.currentPower.equals("Meteor Shower"))
        	clone.drawImage(images.checkAndEx[0] ,(int) menuBox.getX() + 24, (int) menuBox.getY() + 44, 12, 12, null);
        
        else if(powerHandler.currentPower.equals("Lightning"))
        	clone.drawImage(images.checkAndEx[0] ,(int) menuBox.getX() + 24, (int) menuBox.getY() + 80, 12, 12, null);
        
        else if(powerHandler.currentPower.equals("Rain"))
        	clone.drawImage(images.checkAndEx[0] ,(int) menuBox.getX() + 24, (int) menuBox.getY() + 112, 12, 12, null);
        
        else if(powerHandler.currentPower.equals("Erase"))
        	clone.drawImage(images.checkAndEx[0] ,(int) menuBox.getX() + 24, (int) menuBox.getY() + 258, 12, 12, null);
  
        ///////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////DRAW TIMESTRINGS PRESENT ON THE SCREEN AND EDGE CLOUDS///////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////
        clone.setColor(Color.WHITE);
        //clone.drawString(displayTickCount + " FPS", WIDTH - 65, 10); TODO
        clone.drawString(gameSpeed + "x Speed", WIDTH - 43, HEIGHT - 5);
        
        //NEW FONT FOR DISPLAYING SOCIETY PROPERTIES
        clone.setFont(new Font("Consolas", Font.PLAIN, 10));
        
        clone.drawString("Wood: " + island.society.woodCount + "  Minerals: " + island.society.mineralCount + "  Food: "
        		+ island.society.foodCount, WIDTH/2 - 85, HEIGHT - 5);
        
        //Display society intelligence, and god powers.
        clone.drawImage(images.sciAndReligIcons[0], 260, 2, 16, 24, null);
        clone.setColor(Color.CYAN);
      
        if(superMode)
        {
        	clone.setFont(new Font("Consolas", Font.PLAIN, 25));
        	clone.drawString("\u221e", 280, 15);
        }
        else
        {
        	clone.drawString(Integer.toString(powerHandler.godEnergy), 277, 10);
        }
        
        clone.setFont(new Font("Consolas", Font.PLAIN, 10));
        
        clone.drawImage(images.sciAndReligIcons[1], 320, 2, 16, 24, null);
        clone.setColor(Color.GREEN);
        clone.drawString(Integer.toString(island.society.intelligence), 335, 10);
        
	    if(xMouse >= (WIDTH * 2 - 18 * 2) && xMouse <= (WIDTH * 2) && 
				yMouse >= 0 && yMouse <= 24 * 2)
	    	clone.drawImage(images.exitDoor, WIDTH - 20, -2, (int) (16 * 1.2), (int) (24 * 1.2), null);
	    else
	    	clone.drawImage(images.exitDoor, WIDTH - 18, 0, null);
        
        //If the user is spending pts that they don't have, show so.
        if(illigalFlagTimer > 0)
        {
        	clone.drawImage(images.checkAndEx[1], 230, 35, 15, 15, null);
        	clone.setColor(Color.RED);
        	clone.drawString("You don't have enough energy!", 250, 46);
        	illigalFlagTimer--;
        }
	}
	
	public void mouseDragged(MouseEvent e) 
	{
		xMouse = e.getX();
        yMouse = e.getY();
	}
	public void mouseMoved(MouseEvent e) 
	{
		xMouse = e.getX();
	    yMouse = e.getY();
	}
	public void mouseClicked(MouseEvent arg0)
	{ 
		MenuHandler.handleMenuClicks(arg0.getX(), arg0.getY(), powerHandler, this);
	}
	public void mouseEntered(MouseEvent arg0){}
	public void mouseExited(MouseEvent arg0){}
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		int notches = arg0.getWheelRotation();
		//The zoom increment must be proportional, 0.1 is to 1.5, etc.
	    if (notches < 0) //Mouse wheel UP
	    {
	    	if(zoomFactorIsland <= ZOOM_CAP_ISLAND_HIGH)
	    		zoomFactorIsland += 0.1;
	    	if(zoomFactorEntity <= ZOOM_CAP_ENTITY_HIGH)
	    		zoomFactorEntity += 0.1 / 1.5;
	    } 
	    else //Mouse wheel DOWN
	    {
	    	if(zoomFactorIsland >= ZOOM_CAP_ISLAND_LOW)
	    		zoomFactorIsland -= 0.1;
	    	if(zoomFactorEntity >= ZOOM_CAP_ENTITY_LOW)
	    	zoomFactorEntity -= 0.1 / 1.5;
	    } 
	}
	public void mousePressed(MouseEvent arg0) 
	{
	    synchronized (this)
	    {
	    	if(arg0.getButton() == 3 && !focusLost)
	    	{
	    		xScrollStart = arg0.getX();
				scrolling = true;
	    	}
	    	//The sequence that calls and decides which superpower to use.
	    	if(arg0.getButton() == 1 && arg0.getX() > menuBox.getMaxX() + 50)
	    	{
	    		double translatedX = arg0.getX() - xCenter * 2;
	    	    double translatedY =  arg0.getY() - yCenter * 2 + 3;
	    	    double randomXSpawn = Math.random() * WIDTH - (WIDTH / 2);    
	    	    powerHandler.launchPower(translatedX, translatedY, randomXSpawn);
	    	}
	    }
	}
	public void mouseReleased(MouseEvent arg0) 
	{
		if(arg0.getButton() == 3)
	       scrolling = false; 
	}
	public void keyPressed(KeyEvent arg0)
	{
		int keyCode = arg0.getKeyCode();
    	if (xCenter <= 720 && (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)) KEY_ACTIONS[0] = true;
	    if (xCenter >= -90 && (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)) KEY_ACTIONS[2] = true;  
	    if (yCenter <= 388 && (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)) KEY_ACTIONS[3] = true;
	    if (yCenter >= -50 && (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)) KEY_ACTIONS[1] = true;
	}
	public void keyReleased(KeyEvent arg0)
	{
		int keyCode = arg0.getKeyCode();
    	if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) KEY_ACTIONS[0] = false;
	    if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) KEY_ACTIONS[2] = false;    	
	    if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) KEY_ACTIONS[3] = false;
	    if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) KEY_ACTIONS[1] = false;
	}
	public void keyTyped(KeyEvent arg0) {}
	
	public static void main(String[] args)
	{  
	    final MainComponent component = new MainComponent(layout, GameApplet.WIDTH/2, GameApplet.HEIGHT/2);

	    Frame frame = new Frame("Island Overlord");
	    frame.add(component);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(true);
	    frame.addWindowListener(new WindowAdapter()
	    {
	       public void windowClosing(WindowEvent we)
	       {
	          component.stop();
	          System.exit(0);
	       }
	    });
	    frame.setVisible(true);
	    component.start(false); 
	}
	public void focusGained(FocusEvent arg0) 
	{
		focusLost = false;
	}
	public void focusLost(FocusEvent arg0) 
	{
		focusLost = true; 
	}
}
