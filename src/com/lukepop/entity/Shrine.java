package com.lukepop.entity;

import java.awt.Graphics2D;

public class Shrine extends Entity
{
	public int shineTimer;
	
	public Shrine(double x, double y) 
	{
		super(x, y, 4, "Shrine");
		
		this.shineTimer = 0;
		this.gatherable = true;
		this.workType = "Pray";
	}
	
	public void tick()
	{
		super.tick();
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 8);
	    int y = -(int) (yr / 2 + 32 - 4);

	    if(shineTimer-- > 0)
	    {
	    	g.drawImage(images.shrines[1], x, y, null);
	    }
	    else
	    	g.drawImage(images.shrines[0], x, y, null);
	    
	    if(onFire)
	    	 super.render(g, alpha);
	}
	
	public void exhaust()
	{
		//Do nothing, shrines can't die.
	}
}
