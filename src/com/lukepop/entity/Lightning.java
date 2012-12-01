package com.lukepop.entity;

import java.awt.Graphics2D;

import com.lukepop.game.MainComponent;
import com.lukepop.island.DoublePoint;
import com.lukepop.island.Sound;
import com.lukepop.supernatural.Supernatural;

//Lightning and Rain objects are special because they both will
//create a Cloud object to simulate real weather. This is neat design because
//the MainComp never actually interacts with cloud objects, just Rain and Lightning,
//which are god powers.
public class Lightning extends Entity
{
	private MainComponent mainComponent;
	public Cloud ourCloud;
	public double cloudHeight = this.random.nextInt(50) + 100;
	public double cloudX, cloudY, targetX, targetY;
	public boolean lightningOn = false;
	public int frameTimer = 0;
	public int xJump;
	
	public Lightning(double x, double y, MainComponent mainComponent) 
	{
		super(x, y, 5, "Lightning");
		
		this.mainComponent = mainComponent;
		DoublePoint cloudPoint  = mainComponent.island.convertToGameCoords(x, y - cloudHeight + 20);
		cloudX = cloudPoint.xCoord;
		cloudY = cloudPoint.yCoord;
		DoublePoint lightningPoint = mainComponent.island.convertToGameCoords(x, y);
		targetX = lightningPoint.xCoord;
		targetY = lightningPoint.yCoord;
		
		ourCloud = new Cloud(cloudX, cloudY, targetX, targetY, cloudHeight);
		this.mainComponent.island.addEntity(ourCloud);
	}
	
	public void tick()
	{
		if(ourCloud.cloudTimer == Cloud.CLOUD_DESCENT_TIMER_INTERVAL)
			lightningOn = true;
		
		else if(ourCloud.cloudTimer == Cloud.CLOUD_EVENT_DISSAPPEAR_INTERVAL)
			alive = false;
		
		if(lightningOn)
		{
			if(frameTimer % 10 == 0)
			{
				xJump = random.nextInt(30) - 15;
				island.supernaturalOnCollided(targetX + xJump, targetY, Supernatural.LIGHTNING.desc, this);
			}	
			if(mainComponent.musicOn && frameTimer % 49 == 0)
		    	Sound.play(Sound.THUNDER, 0);
			frameTimer++;
		}
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (ourCloud.xr) + xJump;
        int y = -(int) (ourCloud.yr / 2 + 24 - 4);     
        
        if(lightningOn)
        	g.drawImage(images.lightning, x, y, 3, (int) cloudHeight - 50, null);
	}
}
