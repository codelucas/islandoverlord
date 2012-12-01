package com.lukepop.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class Animal extends Entity
{
	public static int THIRST_FREQUENCY;
	public static final int THIRST_DURATION = 400;
	public static int STRAIF_RADIUS;
	public static int REPRODUCE_FREQ = 1000;
	public static double REPRODUCE_SPEED = 0.3;
	public static final int WORK_TIMER = 100;
	private static final int[] animDirs = { 2, 0, 3, 1 };
	
	public String type = null;
	//Hard-coded for now.
	double speed = 0.01;
	double xLocationWater, yLocationWater;
	double deltaX, deltaY;
	public int alteredX, alteredY;
	double rot = 0;
	public int moveTick;
	public int thirsty = 0; int thirstCounter = 0;
	public Rectangle animal;
	public Pond pond;
	
	public Animal(double x, double y, Pond pond) 
	{  
		super(x, y, 3, "Animal");
		
		if(Math.random() * 1 >= 0.8)
			type = "Monkey";
		else 
			type = "Sheep";	
		
		workType = "Hunt";
		
		THIRST_FREQUENCY = random.nextInt(2500) + 1000;
		STRAIF_RADIUS = random.nextInt(20) - 10;
		
		gatherable = true;
		rot = random.nextDouble() * Math.PI * 2;
		animal = new Rectangle((int) x, (int) y, 3, 3);
		moveTick = random.nextInt(12);
		this.pond = pond;
	}
	
	public void tick()
	{
		super.tick();
		if(thirsty > 0)
		{
			//Set the destination location, if its not open change it
			xLocationWater = pond.x + alteredX;
			yLocationWater = pond.y + alteredY;
			deltaX = (xLocationWater - this.x);
			deltaY = (xLocationWater - this.y);
		
			if(island.isFree(x + deltaX * speed, y + deltaY * speed, radius, this))
			{ 
				x += deltaX * speed;
				y += deltaY * speed;
				alteredX = 0; alteredY = 0;
			}
			else
			{ 	
				//This is hard-coded for now, will change later.
				//If an animal gets stuck, strafe him to left or right.
				alteredX += STRAIF_RADIUS;
				alteredY += -STRAIF_RADIUS;
			}
			thirsty--; 
		}
		else
		{ 
			double speed = 1;
			rot += (random.nextDouble() - 0.5) * random.nextDouble()*2;

		    double xt = x + Math.cos(rot) * 0.4 * speed;
		    double yt = y + Math.sin(rot) * 0.4 * speed;
		    if (island.isFree(xt, yt, radius, this))
		    {
		       x = xt;
		       y = yt;
		    }
		    moveTick += speed;
		    if(thirstCounter % THIRST_FREQUENCY == 0)
			{
				thirsty = THIRST_DURATION;
			}
		}
		thirstCounter++;
		
		if(thirstCounter > Integer.MAX_VALUE - 1)
			thirstCounter = 0;
		
		//Controlling animal reproduction.
		if(island.gameLoop % REPRODUCE_FREQ == 0 && island.gameLoop != 0 && Math.random() < REPRODUCE_SPEED)
		{
			double xp = x + random.nextGaussian() * 8;
		    double yp = y + random.nextGaussian() * 8;
		    Animal animal = new Animal(xp, yp, pond);
		    if(island.isFree(animal.x, animal.y, animal.radius)) island.addEntity(animal);
		}
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 4);
	    int y = -(int) (yr / 2 + 8);
	    
	    int rotStep = (int) Math.floor((rot - island.rot) * 4 / (Math.PI * 2) + 0.5);
	    
	    
	    if(type.equals("Monkey"))
	    	g.drawImage(images.monkeys[animDirs[rotStep & 3]], x, y, null);
	    else
	    	g.drawImage(images.sheep[animDirs[rotStep & 3]], x, y, null);
		
	    if(onFire)
	    	 super.render(g, alpha);
	}
}