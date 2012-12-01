package com.lukepop.island;

import com.lukepop.entity.Animal;
import com.lukepop.entity.Building;
import com.lukepop.entity.Entity;
import com.lukepop.entity.Mineral;
import com.lukepop.entity.Person;
import com.lukepop.entity.Shrine;
import com.lukepop.entity.Tree;
import com.lukepop.supernatural.Prayer;

public class Job 
{	
	//Jobs:
	// "Return" to main hut
	// "Build" house
	//  Gather "Woodcut" OR "Mine" OR "Hunt"
	//  "Pray" To the diety (you). (On the spot)
	public double jobX, jobY;
	public String type = null;
	public Entity jobObject;
	public int jobRadius;
	public int workTimer;
	public boolean finished = false;
	public boolean started = false;
	public Person jobOwner = null;
	//Returned Resources:
		//"W" = Wood
		//"M" = Minerals
		//"F" = Food
		//"N" = Nothing
		//"E" = God Energy
	public String resource = "N";
	
	public Job(String type, Entity jobObject, Person jobOwner, double xLoc, double yLoc)
	{
		this.jobObject = jobObject;
		this.jobX = xLoc; 
		this.jobY = yLoc;
		this.type = type; 
		this.jobOwner = jobOwner;
		
		if(type.equals("Woodcut")) 
		{ 
			Tree t = (Tree) jobObject;
			jobRadius = (int) t.radius;
			workTimer = Tree.WORK_TIMER; 	
			resource = "W";
	    }
		else if(type.equals("Mine")) 
        { 
			Mineral m = (Mineral) jobObject;
			jobRadius = (int) m.radius; 
			workTimer = Mineral.WORK_TIMER; 
			resource = "M";
        }  
        else if(type.equals("Hunt")) 
        { 
        	Animal a = (Animal) jobObject;
        	jobRadius = (int) a.radius + 5; 
        	workTimer = Animal.WORK_TIMER; 
        	resource = "F";
        }
        else if(type.equals("Build")) 
        { 
        	Building b = (Building) jobObject; 
        	jobRadius = (int) b.radius + 5; 
        	workTimer = b.totalBuildTime; 
        	resource = "N";
        }         							
        else if(type.equals("Return")) 
        { 
        	//9 is the default home radius.
        	jobRadius = 9 + 5; 
        	workTimer = 20; //The worktimer for return is pointless, a dummy variable.
        	resource = "N";
        }
        else if(type.equals("Pray"))
        {
        	jobRadius = Prayer.RADIUS;
        	workTimer = Prayer.WORK_TIME; 
        	resource = Prayer.RESOURCE;
        }
	}
	
	public boolean isGatherable()
	{
		if(jobObject == null)
			return false;
		return jobObject.gatherable;
	}
	
	public boolean isSameJob(String jobDescription)
	{
		if(jobDescription != null)
		{
			if(this.type.equals(jobDescription))
				return true;	
		}
		return false;
	}
	
	public void startJob()
	{   
		started = true;
		
		if(type.equals("Build"))
		{
			Building currentBuilding = (Building) jobObject;
			currentBuilding.startedBuilding = true;
		}
		if(type.equals("Pray"))
		{
			Shrine shrine = (Shrine) jobObject;
			shrine.shineTimer = workTimer;
			jobOwner.holdingResource = "N";
		}
	}
	
	public String obtainResource()
	{	
		return resource;
	}
	
	public void swapJob(Entity newJobObject)
	{
		this.jobObject.targeted = false;
		newJobObject.targeted = true;
		jobOwner.setJob(new Job(newJobObject.workType, newJobObject, jobOwner, newJobObject.x, newJobObject.y));
	}
	
	public void tick()
	{	
		if(started && workTimer > 0)
			workTimer--;
		if(workTimer == 0)
			finished = true;
		
		//If the job was not building or returning something, exhaust it. 
		//if(!type.equals("Build") && !type.equals("Return") && finished)	
		if(jobObject.gatherable && finished)
			jobObject.exhaust();
		
		if(finished)
		{
			//Reset the counter for entity time outs.
			jobOwner.impossibleJobCounter = Person.IMPOSSIBLE_MAX;
			
        	if(jobObject.gatherable)
        	{
        		jobOwner.holdingResource = resource;
        		//Return home after gathering jobs.
        		if(jobOwner.island.mainHut != null)
        			jobOwner.returnHome();
        	}
    		//No need to set targeted to false, because the job will be deleted.
        	else if(type.equals("Return"))
        	{
        		//Careful!: The "resource" is the resource of the current job,
        		//jobOwner.holding.. is what the person is holding, they are usually
        		//differnt because a return trip yeilds a resource of "N", but the
        		//person is holding what he gathered usually!
        		jobOwner.island.society.addResources(jobOwner.holdingResource); 
        		jobOwner.holdingResource = "N";
        	}
        	
        	//After the job is done, the job is set to nothing! (Unless its gatherable, then
        	//we need to return to the hut!
        	if(!jobObject.gatherable) { jobOwner.setJob(null); }
        	
			if(type.equals("Build") && jobObject instanceof Building)
			{
				Building extract = (Building) jobObject;
				if(extract.type.equals("Basic Hut"))
					jobObject.island.society.intelligence += 50;
				
				else if(extract.type.equals("Barracks"))
					jobObject.island.society.intelligence += 150;
				
				else if(extract.type.equals("Pub"))
					jobObject.island.society.intelligence += 100;
				
				else if(extract.type.equals("Library"))
					jobObject.island.society.intelligence += 500;
			}
		}
	}
}
