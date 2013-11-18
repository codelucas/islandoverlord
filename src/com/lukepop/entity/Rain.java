package com.lukepop.entity;

import java.awt.Graphics2D;
import java.util.ArrayList;

import com.lukepop.game.MainComponent;
import com.lukepop.island.DoublePoint;
import com.lukepop.island.Sound;

public class Rain extends Entity
{
	private MainComponent mainComponent;
	
	public double cloudHeight = this.random.nextInt(50) + 100;
	public double cloudX, cloudY, targetX, targetY;
	public Cloud ourCloud;
	public boolean initiateFalling = false;
	
	public ArrayList<RainDrop> rainDrops = new ArrayList<RainDrop>();
	public int rainDropTimer = 0;
	
	public Rain(double x, double y, MainComponent mainComponent) 
	{
		super(x, y, 0, "Rain");
		
		this.mainComponent = mainComponent;
		DoublePoint cloudPoint  = mainComponent.island.convertToGameCoords(x, y - cloudHeight);
		cloudX = cloudPoint.xCoord;
		cloudY = cloudPoint.yCoord;
		DoublePoint newCoord = mainComponent.island.convertToGameCoords(x, y);
		targetX = newCoord.xCoord;
		targetY = newCoord.yCoord;
		
		ourCloud = new Cloud(cloudX, cloudY, targetX, targetY, cloudHeight);
		
		this.mainComponent.island.addEntity(ourCloud);
	}
	
	public void tick()
	{
		if(ourCloud.cloudTimer == Cloud.CLOUD_DESCENT_TIMER_INTERVAL)
			initiateFalling = true;
		
		if(rainDropTimer % 4 == 0 && initiateFalling)
		{
			int randomFactor = random.nextInt(30) - 15;
			double newTargetX =  targetX + randomFactor + 4;
			double newCloudX = ourCloud.xr + randomFactor + 4;
			RainDrop drop = new RainDrop(newTargetX, targetY, newCloudX, ourCloud.yr, ourCloud);
			rainDrops.add(drop);
			mainComponent.island.addEntity(drop);  			
		}
		rainDropTimer++;
		
		if(ourCloud.cloudTimer == Cloud.CLOUD_EVENT_DISSAPPEAR_INTERVAL)
			alive = false;
	}
	
	public void render(Graphics2D g, double alpha)
	{
		//Render individual raindrops. 
	}
}

class RainDrop extends Entity
{
	public double destinationX, destinationY;
	public double deltaX, deltaY;
	
	public static int RADIUS = 0;
	public double rot = 0;
	public int speed = 7;
	public Cloud ourCloud;
	public int fallAmount = 15;
	
	public RainDrop(double desX, double desY, double currX, double currY, Cloud cloud)
	{
		super(currX, currY, RADIUS, "RainDrop");
		
		this.destinationX = desX;
		this.destinationY = desY;
		this.ourCloud = cloud;
	}
	
	public void tick()
	{
		if(fallAmount > 0)
		{
			y -= 8;  
			fallAmount--;
		}
		
		if(fallAmount == 0 || (ourCloud.cloudTimer == Cloud.CLOUD_EVENT_DISSAPPEAR_INTERVAL))
			alive = false;
	}
	
    public void updatePos(double sin, double cos, double alpha)
    {
    	xr = x;
    	yr = y;
    }
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 4);
        int y = -(int) (yr / 2 + 8 - 2);     
        
       	g.drawImage(images.rainDrop, x, y, 1, 3, null);
	}
}
