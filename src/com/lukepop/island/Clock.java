package com.lukepop.island;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

//Only minutes and hours are used.
public class Clock 
{
	int xh, yh, xm, ym;
	int m = 10, h = 10;
	int xcenter = 21, ycenter = 20;
	double minuteSpinFactor = 0;
	double hourSpinFactor = 0;
	int timeSpeed = 1; //0: Paused, 1: Normal 2: fast 3: faster 4: even faster
	    	    
	public void render(Graphics2D clone, BufferedImage clock, int xLocation, int timeSpeed)	
	{		
		if(timeSpeed == 1)
		{
			minuteSpinFactor = 30;
			hourSpinFactor = 18;
		}
		else if(timeSpeed == 2)
		{
			minuteSpinFactor = 20;
			hourSpinFactor = 13;
		}
		else if(timeSpeed == 3)
		{
			minuteSpinFactor = 10;
			hourSpinFactor = 8;
		}
		else if(timeSpeed == 4)
		{
			minuteSpinFactor = 1;
			hourSpinFactor = 1;
		}
		else if(timeSpeed == 0)
		{
			minuteSpinFactor = 100000;
			hourSpinFactor = 10000;
		}
		
		clone.drawImage(clock, xLocation, 10, 19, 19, null);
        
		int xHinge = xLocation + 10;
		
        xm = (int) (Math.cos((m += 1) / minuteSpinFactor * Math.PI / 30 - Math.PI / 2) * 8 + xHinge);      
        ym = (int) (Math.sin((m += 1) / minuteSpinFactor * Math.PI / 30 - Math.PI / 2) * 8 + ycenter);
        
        xh = (int) (Math.cos((h * 30 + m / hourSpinFactor) * Math.PI / 180 - Math.PI / 2) * 4
                   + xHinge);
        yh = (int) (Math.sin((h * 30 + m / hourSpinFactor) * Math.PI / 180 - Math.PI / 2) * 4
                   + ycenter);
        
        clone.setColor(Color.WHITE);
        clone.drawLine(xHinge, ycenter-1, xm, ym);
        clone.drawLine(xHinge - 1, ycenter, xm, ym);
        clone.drawLine(xHinge, ycenter-1, xh, yh);
        clone.drawLine(xHinge - 1, ycenter, xh, yh);
	}	
}