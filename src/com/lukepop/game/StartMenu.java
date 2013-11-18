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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;
import java.util.Collections;

import com.lukepop.entity.Cloud;
import com.lukepop.entity.Entity;
import com.lukepop.entity.Lightning;
import com.lukepop.game.GameApplet.Layout;
import com.lukepop.island.Images;
import com.lukepop.island.Island;
import com.lukepop.island.Sound;

public class StartMenu extends Canvas implements Runnable, MouseListener, MouseMotionListener, FocusListener
{
	private static final long serialVersionUID = 1L;
	public boolean running;
	private VolatileImage image;
	public static final int TICKS_PER_SECOND = 30;
	private static final int MAX_TICKS_PER_FRAME = 10;
	private static Layout layout;
	private Thread thread;
	
	public int WIDTH, HEIGHT;
	public int xCenter, yCenter;
	private double xRot, xRotA;
	public Island island;
	public Images images;
	private double iconScale = 2.5;
	private double defaultMag, sandMag, demolitMag, instMag, credMag;
	private int defaultEq, sandEq, demolitEq, instEq, credEq;
	private String defaultSt, sandSt, demolitSt, instSt, credSt;
	
	//Transition Variables.
	public String switchToComp = "";
	private float beta = 0.f;

	public StartMenu(Layout inlayout, int width, int height)
	{
		this.WIDTH = width; 
		this.HEIGHT = height;
		this.xCenter = WIDTH / 2; 
		this.yCenter = HEIGHT * 48 / 80;
		
		layout = inlayout; 
		images = layout.images;
		island = new Island(images);
		defaultMag = sandMag = demolitMag = instMag = credMag = 1;
		defaultEq = sandEq = demolitEq = instEq = credEq = 0;
		defaultSt = sandSt = demolitSt = instSt = credSt = "";
		
		setSize(width * 2, height * 2);
        addMouseMotionListener(this); 
        addMouseListener(this);
        addFocusListener(this); 
	}

    public void start()
    {
        thread = new Thread(this);
        thread.start(); 
    }
    
    public void stop()
    {
        running = false;
        thread = null;
//        try
//        {
//            if(thread != null) { thread.join(); }
//        }
//        catch (InterruptedException e){}
    }
    
    public void paint(Graphics g) {}

    public void update(Graphics g) {}
    
	public void run() 
	{	
		float lastTime = (System.nanoTime() / 1000000) / 1000.0f;
        double msPerTick = 1.0 / TICKS_PER_SECOND;
		running = true;
        
		while(running)
		{
			synchronized(this)
	        {
                float now = (System.nanoTime() / 1000000) / 1000.0f;
                int frameTicks = 0; 
            	while (now - lastTime > msPerTick) 
                {
                    if(frameTicks++ < MAX_TICKS_PER_FRAME) { tick(); }
                    lastTime += msPerTick;
                }  
				this.render((now - lastTime) / msPerTick);  
	        }
            try { Thread.sleep(3); }
            catch (InterruptedException e) { e.printStackTrace(); }
		} 
	}
	
	public void tick()
	{
        xRot += xRotA;
        xRotA *= 0.7;
        xRotA -= 0.0015;
        island.rot = xRot;
        island.startTick();
	}
	
	public void render(double alpha)
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
            Graphics2D clone = (Graphics2D) g.create();
            renderGame(g, clone, alpha);
            g.dispose();

            Graphics gg = bs.getDrawGraphics();
            gg.drawImage(image, 0, 0, WIDTH * 2, HEIGHT * 2, 0, 0, WIDTH, HEIGHT, null);
            gg.dispose();
            bs.show();
        }
	} 
	
	public void renderGame(Graphics2D g, Graphics2D clone, double alpha)
	{	
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
        	 if(e instanceof Cloud || e instanceof Lightning)
        	 {
        		 //NOTE: The index is not decremented after this list removal because
        		 //we are instantly re-adding it, a decrement causes an infinite loop.
        		 island.entities.remove(i);
        		 island.entities.add(e);
        	 }
        }
       
        AffineTransform af = g.getTransform();
        
        g.translate(xCenter, yCenter);
        																							
        //This scale moves the image forward, so its bigger, default 1.5
        g.scale(1.5, 1.5);
        //This scale shifts the island so its laying down instead of facing up
        g.scale(1, 0.5);      
        //Handles rotation
        g.rotate(-rot);    
         
        g.translate(-248, -280);
        g.drawImage(images.ocean, 0, 0, WIDTH, (int) (HEIGHT * 1.75), null);
        
        g.translate(120, 152);
        g.drawImage(images.island, 0, 0, null);        
        //Applies all previous translates, scales, and rotates and sets the transform. 
        //Most importantly, it resets the transform.
        g.setTransform(af);  
        
        g.translate(xCenter, yCenter);

        //Draw all entities on the island
        for (int i = 0; i < island.entities.size(); i++)
            island.entities.get(i).render(g, alpha);
        
        g.setTransform(af);
        
        //Translucent black screen.
        clone.setColor(new Color(0, 0, 0, 90));
        clone.fill(new Rectangle(0, 0, WIDTH, HEIGHT));
        
        clone.drawImage(images.logoSequence[0], 200, 50, 72 * 3, 24 * 3, null); //Island
        clone.drawImage(images.logoSequence[1], 237, 100, (int) (80 * 1.5), (int) (32 * 1.5), null); //Overlord
        
        //Drawing the icons and buttons.
        clone.setColor(Color.WHITE);
        clone.setFont(new Font("Consolas", Font.PLAIN, 9));
        //DefaultGame
        clone.drawImage(images.logoSequence[2], 275 - defaultEq, 165 - defaultEq, (int) (16 * iconScale * defaultMag), (int) (8 * iconScale * defaultMag), null); 
        clone.drawString(defaultSt, 275 + (int) (16 * iconScale) + 10, 165 + (int) ((8 * iconScale) / 2));
        //SandBox
        clone.drawImage(images.logoSequence[3], 275 - sandEq, 190 - sandEq, (int) (16 * iconScale * sandMag), (int) (8 * iconScale * sandMag), null); 
        clone.drawString(sandSt, 275 + (int) (16 * iconScale) + 10, 190 + (int) ((8 * iconScale) / 2));
        //Demolition
        clone.drawImage(images.logoSequence[4], 275 - demolitEq, 215 - demolitEq, (int) (16 * iconScale * demolitMag), (int) (8 * iconScale * demolitMag), null); 
        clone.drawString(demolitSt, 275 + (int) (16 * iconScale) + 10, 215 + (int) ((8 * iconScale) / 2));
        //Instructions
        clone.drawImage(images.logoSequence[5], 275 - instEq, 240 - instEq, (int) (16 * iconScale * instMag), (int) (8 * iconScale * instMag), null);
        clone.drawString(instSt, 275 + (int) (16 * iconScale) + 10, 240 + (int) ((8 * iconScale) / 2));
        //Credits
        clone.drawImage(images.logoSequence[6], 275 - credEq, 265 - credEq, (int) (16 * iconScale * credMag), (int) (8 * iconScale * credMag), null); 
        clone.drawString(credSt, 275 + (int) (16 * iconScale) + 10, 265 + (int) ((8 * iconScale) / 2));
        
        
        //Myself: :D
        clone.setFont(new Font("Consolas", Font.PLAIN, 8));
        clone.drawString("2012 Lucas Ou-Yang", WIDTH - 75, HEIGHT - 5);
        if(!switchToComp.equals(""))
        {
        	g.setColor(new Color(0, 0, 0, beta));
    		g.fillRect(0, 0, 1200, 640);
    		//Only play the tap once!
    		if(beta == 0) {  Sound.play(Sound.TAP, 0); }
    		
    	    //Increase the opacity and repaint
    	    if ((beta += 0.01f) >= 1f) 
    	    {
    	    	beta = 0f; 
    	        StartMenu.layout.swapView(switchToComp);
    	        switchToComp = "";
    	    } 
        }
	}

	public void focusGained(FocusEvent arg0) {}

	public void focusLost(FocusEvent arg0) {}

	public void mouseDragged(MouseEvent arg0) {}

	//x, y coord values must be multiplied by 2. 
	//"Mag" refers to magnification, "Eq" to equalizing the x and y axis, and st to the displayed String!
	public void mouseMoved(MouseEvent arg0) 
	{
		int xC = arg0.getX();
		int yC = arg0.getY();
		int width = (int) (16 * iconScale);
		int height = (int) (8 * iconScale);
		
		//DefaultGame
		if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 165*2 && yC <= (165 + height)*2) { defaultMag = 1.3; defaultEq = 5; defaultSt = "Classic Mode"; }
		//SandBox
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 190*2 && yC <= (190 + height)*2) { sandMag = 1.3; sandEq = 5; sandSt = "Sand-box Mode"; }
		//Demolition
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 215*2 && yC <= (215 + height)*2) { demolitMag = 1.3; demolitEq = 5; demolitSt = "Demolition Mode"; }
		//Instructions
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 240*2 && yC <= (240 + height)*2) { instMag = 1.3; instEq = 5; instSt = "Instructions"; }
		//Credits
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 265*2 && yC <= (265 + height)*2) { credMag = 1.3; credEq = 5; credSt = "Credits"; }
		//Reset All
		else
		{ 
			defaultMag = sandMag = demolitMag = instMag = credMag = 1;
			defaultEq = sandEq = demolitEq = instEq = credEq = 0;
			defaultSt = sandSt = demolitSt = instSt = credSt = "";
		}
	}

	public void mouseClicked(MouseEvent arg0) 
	{
		int xC = arg0.getX();
		int yC = arg0.getY();
		int width = (int) (16 * iconScale);
		int height = (int) (8 * iconScale);
		//DefaultGame
		if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 165*2 && yC <= (165 + height)*2) { switchToComp = "DefaultGame"; }
		//SandBox
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 190*2 && yC <= (190 + height)*2) { switchToComp = "SandBoxGame"; }
		//Demolition
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 215*2 && yC <= (215 + height)*2) { switchToComp = "DemolitionGame"; }
		//Instructions
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 240*2 && yC <= (240 + height)*2) { switchToComp = "Instructions"; }
		//Credits
		else if(xC >= 2*275 && xC <= (275 + width)*2  && yC >= 265*2 && yC <= (265 + height)*2) { switchToComp = "Credits"; }
	}

	public void mouseEntered(MouseEvent arg0) {}
	
	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}
	
	public static void main(String[] args)
	{  
	    final StartMenu startMenu = new StartMenu(layout, GameApplet.WIDTH/2, GameApplet.HEIGHT/2);

	    Frame frame = new Frame("Island Overlord");
	    frame.add(startMenu);
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(true);
	    frame.addWindowListener(new WindowAdapter()
	    {
	       public void windowClosing(WindowEvent we)
	       {
	    	  startMenu.stop();
	          System.exit(0);
	       }
	    });
	    frame.setVisible(true);
	    startMenu.start();
	}
}
