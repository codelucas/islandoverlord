package com.lukepop.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class Pond extends Entity
{
	public Rectangle testPond;
	
	public Pond(double x, double y) 
	{
		super(x, y, 15, "Pond");
		testPond = new Rectangle((int) x, (int) y, 5, 5); 
		workType = "Drink";
	}
	
	public void tick()
	{
		//Don't super tick, were not hitting this into the air.
	}
	
	public void render(Graphics2D g, double alpha)
	{
		//int pondX = (int) (xr - 4);
	    //int pondY = -(int) (yr / 2 + 8);
		
		//g.setColor(Color.BLUE);
		//g.fillOval(pondX, pondY, (int) testPond.getWidth() * 2, (int) testPond.getHeight() * 2);
	}
}
