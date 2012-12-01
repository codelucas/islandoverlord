package com.lukepop.entity;

import java.awt.Graphics2D;

import com.lukepop.island.Expense;
import com.lukepop.island.Island;
import com.lukepop.island.Job;

public class Person extends Entity
{
	private static final int[] animSteps = { 0, 1, 0, 2 };
	private static final int[] animDirs = { 2, 0, 3, 1 };
	
	public String holdingResource = "N";
	public double rot, moveTick = 0;
	public int wanderCounter = 0;
	public static final int IMPOSSIBLE_MAX = 20;
	//If after this time we still can't reach the job, its impossible
	public int impossibleJobCounter = IMPOSSIBLE_MAX; 
	public Emoticon emoticon = new Emoticon();
	public boolean alerted = false;
	
	public static final Expense COST_TO_SPAWN = new Expense(0, 0, 3, 0);
	
	public Job job = null;
	
	public Person(double x, double y) 
	{
		//X and Y values are not stored, they are fed into the
		//super class, which performs rotation math.
		super(x, y, 1, "Person");
        rot = random.nextDouble() * Math.PI * 2;
        moveTick = random.nextInt(4 * 3);
	}
	
	public void setJob(Job job)
	{
		if(job != null && job.jobObject != null && job.jobObject.gatherable)
			job.jobObject.targeted = false;
		
		this.job = job;
		
		if(job != null)
		{
			if(job.type.equals("Build"))
				emoticon.setEmoticon(job.type);
			else if(job.type.equals("Pray"))
				emoticon.setEmoticon(job.type);
		}
	}
	
	public void tick()
	{
		super.tick();
		if(alerted)
		{
			emoticon.setEmoticon("Alert");
			alerted = false;
		}

		if (job != null) { job.tick(); }
		
		//Idle emoticon, (Used for debugging, but I might as well just leave it).
		else { emoticon.setEmoticon("Curious"); }
		
		//============================MOVEMENT PORTION===============================
		//Sequence that moves the person towards the job's location
		//This only occurs if the person is not wandering and has a job.
		double speed = 1;
        if (wanderCounter == 0 && job != null)
        {
            double deltaX = job.jobX - x;
            double deltaY = job.jobY - y;
            //Extra values for "bonus radius".
            double rd = job.jobRadius + radius;
            if(deltaX * deltaX + deltaY * deltaY < rd * rd && !job.started)
            {  
            	//If the job was return, we are instantly done. Otherwise,
            	//we still need to work (building, gatherthing, etc).
            	if(job.type.equals("Return"))
            		job.finished = true;
            	else
            	{
            		speed = 0;
            		job.startJob();
            	}
            }
            //Drives the entity towards the target.
            rot = Math.atan2(deltaY, deltaX);
        }
        //Random wandering.
        else { rot += (random.nextDouble() - 0.5) * random.nextDouble() * 2; }

        if(wanderCounter > 0) 
        	wanderCounter--;
        
	    double xt = x + Math.cos(rot) * speed * 0.4;
	    double yt = y + Math.sin(rot) * speed * 0.4;
	    
	    if(island.isFree(xt, yt, radius, this))
	    {	
	    	x = xt;
	    	y = yt;
	    }
	    //Sequence that aids the person if he can't reach his job. He either swaps to a new
	    //similar job, sets a new job to whats blocking his way, or simply returns.
	    else
        {
	    	//This collision helper wont apply to building jobs (they are null, we didn't make them yet).
            if(job != null && !job.started && job.isGatherable()) 
            {
                Entity collidedEntity = island.getEntityAt(xt, yt, radius, null);   
                Entity closestGatherable = island.getClosestEntityTo(this, true, false); 
                
                //FIRST PRIORITY: If we are getting blocked from our resource by another gatherable resource, just gather it!
                //if(closestGatherable.distance(this) < 300)
                if(closestGatherable != null && closestGatherable.distance(this) < 1500)
                { 					
                	job.swapJob(closestGatherable);
                }
                
                //SECOND PRIORITY: If we collide with the same type job, just do it.
                else if(collidedEntity != null && collidedEntity.gatherable)
                {
                	speed = 0; 	
                	job.swapJob(collidedEntity);
                    job.startJob();  
                }
                //======================Impossibility handler=================================
                //=========Testing if entities are impossible to reach or not, dynamic timers 
                //=========handled by proximity of entity to person and time taken to reach an entity.
                impossibleJobCounter--;
                if(impossibleJobCounter <= 0)
                { 
                	impossibleJobCounter = IMPOSSIBLE_MAX;
                	job.jobObject.impossibleJob = true;
            		job.jobObject.targeted = false;
                	this.returnHome();
                }
            }
            rot = (random.nextDouble()) * Math.PI * 2;
            wanderCounter = random.nextInt(30) + 3;
        }
	    moveTick += speed;
	}
	
	public void render(Graphics2D g, double alpha)
	{
		int rotStep = (int) Math.floor((rot - island.rot) * 4 / (Math.PI * 2) + 0.5);
        int animStep = animSteps[(int) (moveTick / 4) & 3];

        int x = (int) (xr - 4);
        int y = -(int) (yr / 2 + 8);
        
        int personSpriteIndex;
        
        if(holdingResource.equals("N")) //Remember, "N" means hold nothing.
        	personSpriteIndex = 0;
        else
        	personSpriteIndex = 1;
        
        g.drawImage(images.person[personSpriteIndex][animDirs[rotStep & 3] * 3 + animStep], x, y, null);
            
        //If the person is carrying something, draw the resource.
        if(personSpriteIndex == 1)
        { 
        	int carryIndex = 0;
        	if(holdingResource.equals("W"))
        		carryIndex = 0;
        	else if(holdingResource.equals("M"))
        		carryIndex = 1;
        	else if(holdingResource.equals("F"))
        		carryIndex = 2;
        	else if(holdingResource.equals("E"))
        		carryIndex = 3;
        	g.drawImage(images.carriedStuff[carryIndex], x, y - 3, null);
        }
        
        if(emoticon.isOn())
        {
        	if(personSpriteIndex == 1)
        		g.drawImage(images.emoticons[emoticon.returnIndex()], x, y - 10, null);
        	else
        		g.drawImage(images.emoticons[emoticon.returnIndex()], x, y - 7, null);
        	emoticon.tick();
        }
        
        if(onFire)
        	super.render(g, alpha);
	}
	
	public void returnHome()
	{		
		job = new Job("Return", island.mainHut, this, Island.VILLIAGE_CENTER_X, Island.VILLIAGE_CENTER_Y);
	}
}

class Emoticon
{
	public static final int EMOTE_LENGTH = 150;
	public int duration;
	int index;
	
	public void setEmoticon(String type)
	{
	    if(type.equals("Alert"))
			index = 0;
	    else if(type.equals("Build"))
			index = 1;
	    else if(type.equals("Sad"))
			index = 2;
	    else if(type.equals("Happy"))
			index = 3;
	    else if(type.equals("Eating"))
			index = 4;
	    else if(type.equals("Curious"))
			index = 5;
	    else if(type.equals("Pray"))
			index = 6;
	    
	    duration = EMOTE_LENGTH;
	}
	//Only called if the duration is above zero.
	public void tick()
	{
		duration--;
	}
	
	public boolean isOn()
	{
		if(duration > 0)
			return true;
		return false;
	}
	
	public int returnIndex()
	{
		return index;
	}
}
