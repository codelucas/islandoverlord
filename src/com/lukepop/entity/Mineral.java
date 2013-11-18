package com.lukepop.entity;

import java.awt.Graphics2D;


public class Mineral extends Entity
{
	//Gem = 0, Gold = 1, and Stone = 2, Meteor = 4?
	public int type;
	public static final int WORK_TIMER = 50;
	public int yeild;
	
	public Mineral(double x, double y) 
	{
		super(x, y, 5, "Mineral");
		
		if(Math.random() * 1 > 0.95)
			type = 0;
		else if(Math.random() * 1 > 0.8)
			type = 1;
		else if(Math.random() * 1 >= 0)
			type = 3;
		
		workType = "Mine";
		gatherable = true;
		yeild = 1000;
	}
	
	//The constructor for meteors.
	public Mineral(double x, double y, int RADIUS )
	{
		super(x, y, RADIUS, "Mineral");
		workType = "Mine";
		gatherable = true;
	}

	public void tick()
	{
		super.tick();
		if(yeild <= 0)
		{
			alive = false;
			island.mineralCount--;
		}
	}
	
	public void exhaust()
	{
		yeild -= 50;
	}
	
	public void burn()
	{
		//Rocks can't burn.
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 4);
        int y = -(int) (yr / 2 + 8 - 2);
        
        g.drawImage(images.minerals[type], x, y, null);
        
        if(onFire)
        	super.render(g, alpha);
	}
}
