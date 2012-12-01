package com.lukepop.entity;

import java.awt.Graphics2D;

import com.lukepop.game.MainComponent;
import com.lukepop.island.DoublePoint;
import com.lukepop.island.Sound;
import com.lukepop.supernatural.Supernatural;

public class Meteor extends Mineral
{
	public int speed;
	public double targetX, targetY;
	public double deltaX, deltaY;
	public double rot = 0;
	public boolean impacted = false;
	public boolean onceOnCollide = true;
	private MainComponent mainComponent;
	public int mineralYield;
	public MeteorScope scope;
	
	//This special class does not have a it's own "desc" variable because it is a child of 
	//the mineral class, which is a child of the entity. Which means the Meteor's desc is "Mineral",
	//which is proper, because people should be able to mine meteors!
	public Meteor(double x, double y, double targetX, double targetY, MainComponent mainComponent)
	{
		super(x, y, 25);
		speed = 25;
		mineralYield = 15;
		
		//Importing this main component is necessary because we need the island's
		//rotation now, but loading the island only occures after (by design).
		this.mainComponent = mainComponent;
		//Update the target values.
		DoublePoint newCoord = mainComponent.island.convertToGameCoords(targetX, targetY);
			
		this.targetX = newCoord.xCoord;
		this.targetY = newCoord.yCoord;
		
		if(mainComponent.musicOn)
			Sound.play(Sound.COMET_FLYING, 0);
		
		scope = new MeteorScope(this.targetX, this.targetY, 0, "Meteor Scope");
		mainComponent.island.addEntity(scope);
	}
	
	public void tick() 
	{
		//This is normally called in super.tick(), but since meteors are a special scenario,
		//we must manually call this.
		for(Person person : island.people)
			if(this.distance(person) < POSSIBLE_DIST_FACTOR) { impossibleJob = false; }
		
		if(targeted && targetedCounter-- == 0) { targeted = false; targetedCounter = 0;}
		
		if(!impacted)
		{
			double deltaX = targetX - x;
			double deltaY = targetY - y;
			double rd = 12 + this.radius;
			
		    x += Math.cos(rot) * speed;
		    y += Math.sin(rot) * speed;
		    
		    //Condensed distance formula.
		    if (deltaX * deltaX + deltaY * deltaY < rd * rd) 		
		    {
		    	impacted = true;
		    	island.mainComponent.powerHandler.powerHasHit = true;
		    	if(mainComponent.musicOn)
		    		Sound.play(Sound.COMET_IMPACT, 0);
		    }
		    rot = Math.atan2(deltaY, deltaX);
		}
		
		//If the meteor lands on thing, blow them up, also add Smoke!
		if(impacted && onceOnCollide)
		{
			island.addEntity(new Smoke(this.x, this.y));
			island.supernaturalOnCollided(targetX, targetY, Supernatural.METEOR_SHOWER.desc, this);
			scope.alive = false;
			onceOnCollide = false;
		}
		
		if(impacted && !mainComponent.island.isOnGround(x, y)) { this.exhaust(); }
				
		if(mineralYield == 0)
		{
			alive = false;
			island.mineralCount--;
		}
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 8 - 4);
        int y = -(int) (yr / 2);     

        //*CONFUSING*: The y coord is being decremented because y was originally negative.
        //In other entities its being incremented by its height because
        //we are incrementing within the negative symbol!
        if(!impacted)
        {
        	y -= 3 * 8 - 4;
        	//Draw the flying meteor.
        	g.drawImage(images.meteorite[0], x, y, null);  
        }
        else
        {
        	if(mineralYield > 5) { y -= 2 * 8 - 4; }
        	else { y -= 8 - 2; }
        	
        	if(mineralYield == 15)
        		g.drawImage(images.meteorite[1], x, y, null);
        	else if(mineralYield == 10)
        		g.drawImage(images.meteorite[2], x, y, null);
           	else if(mineralYield == 5)
        		g.drawImage(images.meteorite[3], x, y, null);
        }
	}

    public void exhaust()
    {
    	mineralYield -= 5;
    }
    
    //Subclass inside the meteorclass.
    class MeteorScope extends Entity
    {
    	public MeteorScope(double x, double y, double r, String desc) 
    	{
    		super(x, y, r, desc); 
    		this.untouchable = true;
    	}
    	
    	public void render(Graphics2D g, double alpha)
    	{
    		int x = (int) (xr - 27);
            int y = -(int) (yr / 2 + 8 - 4);     
            y -= 10;		
            
            if(!impacted)
            	g.drawImage(images.meteorite[4], x, y, 8 * 7, 24, null);             
    	}
    }
}
