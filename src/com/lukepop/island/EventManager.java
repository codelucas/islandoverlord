package com.lukepop.island;

import java.util.ArrayList;

import com.lukepop.entity.Building;
import com.lukepop.entity.Entity;
import com.lukepop.entity.Person;
import com.lukepop.game.MainComponent;
import com.lukepop.supernatural.Prayer;

public class EventManager 
{
	public ArrayList<Person> people;
	public Island island;
	public Society society;
	public MainComponent component;

    public int personSpawnRate = 1000;
	
	public EventManager(Island island)
	{
		this.island = island;
		society = island.society;
		this.component = island.mainComponent;
		this.people = island.people;
	}
	
	public void tick()
	{
		int wood = society.woodCount;
		int minerals = society.mineralCount;
		int food = society.foodCount;
		int intelligence = society.intelligence;
		this.people = island.people;
		
		//One time scenario (building the main building at the very start).
		if(island.mainHut == null)
		{
			if(Building.canAffordType("Main Hut", wood, minerals, food, intelligence))
			{ 
				Entity mainHut = new Building("Main Hut", Island.VILLIAGE_CENTER_X, Island.VILLIAGE_CENTER_Y);
				//Type cast the entity to a building.
				Building extract = (Building) mainHut;
				
				for(int i = 0; i < people.size(); i++)
				{
					if(people.get(i).job == null || people.get(i).job.isGatherable())
						people.get(i).setJob(new Job("Build", mainHut, people.get(i), Island.VILLIAGE_CENTER_X, Island.VILLIAGE_CENTER_Y));
				}
				island.addEntity(mainHut);
				island.mainHut = mainHut;
				//Make sure the population increment/res decrement are added only once per
				//building, not per builder.
				extract.addPropertiesToIsland();
			}
			//There is no main hut and they can't afford a new one.
			else
			{
				for(int i = 0; i < people.size(); i++)
				{
					people.get(i).job = null;
					people.get(i).alerted = true;
				}
			}
		}
		//If the main hut is null, everyone just panics and runs around.
		else
		{
			//Assign resource positions, just guarantee there are a few idles.
			if(this.getNumberOfFreePeople() > 0)
			{
				//getScarceResource returns a character W, F, M representing the res.
				//getRequiredTask is similar, but returns the task name, both are useful.
				Person current = this.getRandomFreePerson();
				Entity targetResource = island.getClosestResourceToHut(island.society.getResPriorityList(), 0);
				
				if(targetResource == null) { current.setJob(null); System.out.println("...."); }
					
				else
				{
					current.setJob(new Job(targetResource.workType, targetResource, current, targetResource.x, targetResource.y));
					if(!targetResource.desc.equals("Animal")) { targetResource.targeted = true; }					
				}
			}
			//Code for creating a Wonder.
			if(Building.canAffordType("Wonder", wood, minerals, food, intelligence))
			{	     
		         for(int i = 0; i < 1;)
		         {
					 double x = Island.VILLIAGE_CENTER_X + island.random.nextGaussian() * 100;
			         double y = Island.VILLIAGE_CENTER_Y + island.random.nextGaussian() * 100;
			         Entity wonder = new Building("Wonder", x, y);
			         Building extract = (Building) wonder;
			         
			         if(island.isFree(wonder.x, wonder.y, wonder.radius))
			         {
			        	 Person current = this.getForcedPerson();
			        	 current.setJob(new Job("Build", wonder, current, x, y));
			        	 island.addEntity(wonder);	
			        	 extract.addPropertiesToIsland();
			        	 i++;
			         }
		         }
			}
			//Code for creating a library.
			if(Building.canAffordType("Library", wood, minerals, food, intelligence))
			{	     
		         for(int i = 0; i < 1;)
		         {
					 double x = Island.VILLIAGE_CENTER_X + island.random.nextGaussian() * 70;
			         double y = Island.VILLIAGE_CENTER_Y + island.random.nextGaussian() * 70;
			         Entity library = new Building("Library", x, y);
			         Building extract = (Building) library;
			         
			         if(island.isFree(library.x, library.y, library.radius))
			         {
			        	 Person current = this.getForcedPerson();	  
			        	 current.setJob(new Job("Build", library, current, x, y));
			        	 island.addEntity(library);	
			        	 extract.addPropertiesToIsland();
			        	 i++;
			         }
		         }
			}
			//Code for creating barracks
			if(Building.canAffordType("Barracks", wood, minerals, food, intelligence))
			{	     
		         for(int i = 0; i < 1;)
		         {
					 double x = Island.VILLIAGE_CENTER_X + island.random.nextGaussian() * 100;
			         double y = Island.VILLIAGE_CENTER_Y + island.random.nextGaussian() * 100;
			         Entity barracks = new Building("Barracks", x, y);
			         Building extract = (Building) barracks;
			         
			         if(island.isFree(barracks.x, barracks.y, barracks.radius))
			         {
			        	 Person current = this.getForcedPerson();
			        	    
			        	 current.setJob(new Job("Build", barracks, current, x, y));
			        	 island.addEntity(barracks);	
			        	 extract.addPropertiesToIsland();
			        	 i++;
			         }
		         }
			}
			//Sequence for building a basic hut...
			if(island.people.size() >= island.maxPopulation && 
					Building.canAffordType("Basic Hut", wood, minerals, food, intelligence))
			{	     
				 int multrange = 60 + island.people.size();
				 if(multrange > 256) { multrange = 256; }
				
		         for(int i = 0; i < 1;)
		         {
					 double x = Island.VILLIAGE_CENTER_X + island.random.nextGaussian() * multrange;
			         double y = Island.VILLIAGE_CENTER_Y + island.random.nextGaussian() * multrange;
			         Entity hut = new Building("Basic Hut", x, y);
			         Building extract = (Building) hut;
			         
			         if(island.isFree(hut.x, hut.y, hut.radius))
			         {
			        	 Person current = this.getForcedPerson();
			        	 current.setJob(new Job("Build", hut, current, x, y));
			        	 island.addEntity(hut);	
			        	 extract.addPropertiesToIsland();
			        	 i++;
			         }
		         }
			}
		}
		
        //Declare a "village center" for the persons, and add them.
        if(island.gameLoop % personSpawnRate == 0 && island.gameLoop != 0 && island.people.size() < island.maxPopulation
        		&& food > Person.COST_TO_SPAWN.resourceCost[2])
        {
            double x = Island.VILLIAGE_CENTER_X + (island.random.nextDouble() * 32 - 16);
            double y = Island.VILLIAGE_CENTER_Y + (island.random.nextDouble() * 32 - 16);
            
            Person person = new Person(x, y);
            if (island.isFree(person.x,person.y, person.radius))
            { 
            	island.addEntity(person);
            	people.add(person); 
            	society.foodCount -= Person.COST_TO_SPAWN.resourceCost[2];
            }
        }
        
//        //Prayer seqeunce. 
//        if(island.centralGameLoop % (society.intelligence * 2 + 750) == 0 && island.centralGameLoop != 0)
//        {
//        	Person current = this.getForcedPerson();
//        	current.setJob(new Job("Pray", Prayer.JOB_OBJECT, current, Prayer.X_LOC, Prayer.Y_LOC));
//        }
	}
	
    public Person getRandomFreePerson()
    {
    	for(int i = 0; i < people.size(); i++)
    	{
    		if(people.get(i).job == null)
    		{
    			return people.get(i); 
    		}
    	}
    	return null;
    }
    
    public Person getForcedPerson()
    {
    	for(int i = 0; i < people.size(); i++)
    	{
    		if(people.get(i).job == null || (!people.get(i).job.type.equals("Build") &&
    				!people.get(i).job.type.equals("Pray")))
    		{
    			people.get(i).job = null;
    			return people.get(i);
    		}
    	}
    	//Won't happen
    	return null;
    }
    
    public int getNumberOfFreePeople()
    {
    	int freePeople = 0;
    	for(Person person : people)
    	{
    		if(person.job == null)
    			freePeople++;
    	}
    	return freePeople;
    }
}
