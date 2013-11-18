package com.lukepop.entity;

import java.awt.Graphics2D;
import java.util.Random;

import com.lukepop.island.Images;
import com.lukepop.island.Island;

//NOTE** All entities follow a tick --> update position --> render phase!
public class Entity implements Comparable<Entity>
{
	public double x, y, radius;

	//Distance for when someone is this close to an entity to mark it as not
	//an impossible task.
	public static final int POSSIBLE_DIST_FACTOR = 35;
	
	//xr and yr are converted cartesians to fit rotation.
    public double xr, yr;
    
    //Descriptions are identical to class names!
    public String desc = "";
    //The work type required to "work" this entity. (Ex. if its animal, workType = "Hunt")
    public String workType = "";
    
    public Island island;
    protected Images images;
    protected Random random = new Random();
    
    public boolean alive = true;
    
    //Clouds, scopes, and that sort are untouchable.
    public boolean untouchable = false;
    public boolean impossibleJob = false;
    
    //Burn related values
    public boolean onFire = false;
    public int fireLiveCounter;  //This will be based on the radius of the object.
    public static final int FIRE_LIVE_FACTOR = 50; //A factor for the above variable.
    public int fireSpreadSpeed;
    public Smoke currentSmoke = null;
    
    //Explosion trajectory values
    public boolean launched = false;
    public int upwardVelocty, accumulatedYr;
    public double savedY;
    
    public boolean targeted = false;
    public int targetedCounter = 50;
    public boolean gatherable = false;
    
    //NOTE: r stands for radius of entity (for collisions).
    public Entity(double x, double y, double radius, String desc)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.desc = desc;
        fireLiveCounter = (int) (radius * FIRE_LIVE_FACTOR);
        fireSpreadSpeed = 100 + random.nextInt(250);
    }

    public void updatePos(double sin, double cos, double alpha)
    {
        xr = x * cos + y * sin;
        yr = x * sin - y * cos;
        
        //Sequence for updating position when an entity is lauched
        if(launched && radius != 0)
        	yr += accumulatedYr;
        
        if(savedY - 12 > yr)
        {
        	launched = false;
        	accumulatedYr = 0;
        }
    }
    
    //Called in the island addEntity method, auto gives all entities the current island object.
    public void init(Island island, Images images)
    { 
        this.island = island;
        this.images = images;
    }

    //Any entity can call this method at will with "super.tick()".
    //This method performs actions which all entities share, like supernatural events.
    public void tick()
    {	
    	//If a person gets close enough to an entity, and it was marked as impossible, make it possible.
    	if(gatherable && impossibleJob)
    	{
    		for(Person person : island.people)
    		{
    			if(this.distance(person) < POSSIBLE_DIST_FACTOR)
    				impossibleJob = false;
    		}
    	}
    	if(targeted && targetedCounter-- == 0) { targeted = false; targetedCounter = 0;}
    	
    	if(launched)
    	{
    		accumulatedYr += upwardVelocty; //Fly the entity in the air.
    		upwardVelocty--; 				//Gradually, this value will go negative.
    	}
    	if(onFire && fireLiveCounter-- > 0)
    	{
    		if(island.gameLoop % fireSpreadSpeed == 0)
    		{
    			//Spread the fiiiiiiiiire.
    			if(island.getClosestEntityTo(this, false, true) != null)
    				island.getClosestEntityTo(this, false, true).burn();
    		}
    		//For the sake of game speed we will limit one smoke per burning
    		//entity. The currentSmoke var is just a switch, after a smoke dies,
    		//and were still burning a new smoke will begin.
    		if(currentSmoke == null)
    		{
    			currentSmoke = new Smoke(this.x, this.y);
        		island.addEntity(currentSmoke);
    		}
    	}
    	else if(fireLiveCounter <= 0 && !untouchable)
    	{
    		alive = false;
    		//Special checker to remove the real main hut reresentation.
    		if(desc.equals("Building"))
    		{	
    			Building isItMain = (Building) this;
    			if(isItMain.type.equals("Main Hut"))
    				island.mainHut = null;
    		}
    	}
    }

    public boolean isAlive()
    {
        return alive;
    }

    public boolean collides(Entity e)
    {
        return collides(e.x, e.y, e.radius);
    }

    public boolean collides(double ex, double ey, double er)
    {
        if (radius < 0) return false;

        double xd = x - ex;
        double yd = y - ey;
        return (xd * xd + yd * yd) < (radius * radius + er * er);
    }

    public int compareTo(Entity s)
    {
        return Double.compare(s.yr, yr);
    }
    
    //If we are rendering something on fire, super call this method
    //at the END of our child render method to draw over.
    //X, Y Values should be computed before rendering in tick()
    public void render(Graphics2D g, double alpha)
    {
    	int x, y, fireWidth, fireHeight;
    	if(desc.equals("Building"))
    	{
			x = (int) (xr - 14);
		    y = -(int) (yr / 2 + 10 + 8);
		    fireWidth = (int) (radius * 3);
		    fireHeight = (int) (radius * 3);
    	}
    	else if(desc.equals("Animal"))
    	{
			x = (int) (xr - 4);
		    y = -(int) (yr / 2 + 5);
		    fireWidth = (int) (radius * 3);
		    fireHeight = (int) (radius * 3);
    	}
    	else if(desc.equals("Person"))
    	{
    		x = (int) (xr - 2);
		    y = -(int) (yr / 2 + 15);
		    fireWidth = (int) (radius * 3);
		    fireHeight = (int) (radius * 3 + 10);
    	}
    	else
    	{
    		x = (int) (xr - 8);
		    y = -(int) (yr / 2 + 10);
		    fireWidth = (int) (radius * 3);
		    fireHeight = (int) (radius * 3);
    	}
    	
    	g.drawImage(images.heavyFire[random.nextInt(4)], x, y, fireWidth, fireHeight, null);	
    }

    public double distance(Entity e)
    {
        double xd = x - e.x;
        double yd = y - e.y;
        return xd * xd + yd * yd;
    }
    
    //Exhausted is a good verb, is means kill in every sense!
    public void exhaust()
    {
    	alive = false;  
    }
 
    //Supernatural related methods after here:
    public void explodeInAir()
    {
    	launched = true;
    	savedY = yr;
    }
    
    public void setNewUpwardVelocity()
    {
    	upwardVelocty = random.nextInt(10) + 10;
    }
    
    public void burn()
    {
    	onFire = true;
    }
}