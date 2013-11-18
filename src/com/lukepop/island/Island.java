 package com.lukepop.island;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Random;

import com.lukepop.entity.Animal;
import com.lukepop.entity.Building;
import com.lukepop.entity.Entity;
import com.lukepop.entity.Mineral;
import com.lukepop.entity.Person;
import com.lukepop.entity.Pond;
import com.lukepop.entity.Shrine;
import com.lukepop.entity.Tree;
import com.lukepop.game.MainComponent;
import com.lukepop.supernatural.Supernatural;

public class Island
{
	public static final int VILLIAGE_CENTER_X = 0; 
	public static final int VILLIAGE_CENTER_Y = -140;
	public static final int POND_X = -70;
	public static final int POND_Y = -75;
    public MainComponent mainComponent;
    public Images images;
    private int[] pixels;
    
    public ArrayList<Entity> entities = new ArrayList<Entity>();
    public ArrayList<Person> people = new ArrayList<Person>();
    
    public Entity mainHut = null;
    public static Entity shrine = new Shrine(45, -155);
    public Pond pond = new Pond(-70, -75);

    public Random random = new Random(8844);

    public double rot;
    public int maxPopulation = 10;
    public int gameLoop = 0;
    
    public int treeCount, mineralCount, animalCount;
    
    public EventManager eventManager;
    public Society society;

    public Island(MainComponent inComp, Images images)
    {
    	this.mainComponent = inComp;
        this.society = new Society(this);
        this.eventManager = new EventManager(this);
        this.images = images;
        this.pixels = ((DataBufferInt) images.island.getRaster().getDataBuffer()).getData();      
        
        addEntity(shrine);
        addEntity(pond);
        
        for (int i = 0; i < 50; i++)
        {
        	//Properly converted x coords to graph.
            double x = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            double y = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            String type = null;
            if(Math.random() * 1 >= 0.5) { type = "Palm"; }
            else { type = "Apple"; }
            addForrest(x, y, type);
        }
        for(int i = 0; i < 8; i++)
        {
        	double x = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            double y = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            addMinerals(x, y);
        }
        for(int i = 0; i < 10;)
        {
        	double x = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            double y = (random.nextDouble() * 256 - 128) * mainComponent.zoomFactorIsland;
            Animal animal = new Animal(x, y, pond);
            if(isFree(animal.x, animal.y, animal.radius))
            {
            	addEntity(animal);
            	i++;
            }
        }
        //Spawn the initial people a little bit off from the destined building.
        double xStart = 40;
        double yStart = -120;
        for(int i = 0; i < 7;)
        {
        	double x = xStart + (random.nextDouble() * 32 - 16);
            double y = yStart + (random.nextDouble() * 32 - 16);
            Person person = new Person(x, y);
            if (isFree(person.x,person.y, person.radius))
            {
            	addEntity(person);
            	people.add(person);
            	//Bruteforce insert until spots are free.
            	i++;
            }
        }
        //Code for dynamic spawning of people, buildings, nature is in the EventManager object.
    }
    
    //Special constructor for the island displayed on the start menu, (without any animals or humans).
    public Island(Images images)
    {
        this.images = images;
        this.pixels = ((DataBufferInt) images.island.getRaster().getDataBuffer()).getData();      
        
        addEntity(shrine);
        addEntity(pond);
        
        for (int i = 0; i < 50; i++)
        {
        	//Properly converted x coords to graph.
            double x = (random.nextDouble() * 256 - 128) * 1.5;
            double y = (random.nextDouble() * 256 - 128) * 1.5;
            String type = null;
            if(Math.random() * 1 >= 0.5) { type = "Palm"; }
            else { type = "Apple"; }
            addForrest(x, y, type);
        }
        for(int i = 0; i < 8; i++)
        {
        	double x = (random.nextDouble() * 256 - 128) * 1.5;
            double y = (random.nextDouble() * 256 - 128) * 1.5;
            addMinerals(x, y);
        }
    }
    
    public void tick()
    {
    	//All entities: tick() : entity event handling
    	for(int i = 0; i < entities.size(); i++)
    	{
    		entities.get(i).tick();
    		//Entities are marked not alive with the exhaust method, which handles all counters.
    		if(!entities.get(i).isAlive())
    		{   
    	    	if(entities.get(i).desc.equals("Tree")) { treeCount--; }
    	    	else if(entities.get(i).desc.equals("Mineral")) { mineralCount--; }
    	    	else if(entities.get(i).desc.equals("Animal")) { animalCount--; }  
    			entities.remove(i);
    			i--; 
    		}
    	}
    	//Ticking the job manager will assign new roles to everyone and handle finished ones.
    	eventManager.tick();
    	
        if(gameLoop == Integer.MAX_VALUE - 5) { gameLoop = 0; }
        	
        gameLoop++;      
        
        //System.out.println("Island Res: " + "Numberoftrees: " + treeCount + " " + "minerals left: " + mineralCount + " " + "animals left: " + animalCount
        //		+ " " + "Population: " + people.size() + " " + "Max Population: " + maxPopulation + " " + "Targed Resource: " + society.getScarceResEntity());
//        int numberOfBuild = 0;
//        for(Person person : people)
//        if(person.job != null)
//        {
//        	//System.out.println(person.job.type);
//        	if(person.job.type.equals("Build"))
//        	{
//        		numberOfBuild++;
//        		//Building b = (Building) person.job.jobObject;
//        		//System.out.println(b.type);
//        	}
//        }
//        System.out.println(numberOfBuild);
//        int possibleCounter = 0;
//        for(Entity e : entities)
//        {
//        	if(!e.impossibleJob)
//        		possibleCounter++;
//        }
//        System.out.println(possibleCounter);
    }
    
    public void startTick()
    {
    	//All entities: tick() : entity event handling
    	for(int i = 0; i < entities.size(); i++)
    	{
    		entities.get(i).tick();
    		//Entities are marked not alive with the exhaust method, which handles all counters.
    		if(!entities.get(i).isAlive())
    		{   
    	    	if(entities.get(i).desc.equals("Tree")) { treeCount--; }
    	    	else if(entities.get(i).desc.equals("Mineral")) { mineralCount--; }
    	    	else if(entities.get(i).desc.equals("Animal")) { animalCount--; }  
    			entities.remove(i);
    			i--; 
    		}
    	}
    	if(gameLoop++ == Integer.MAX_VALUE - 5) { gameLoop = 0; }
    }
    
    private void addForrest(double xo, double yo, String type)
    {
        for(int i = 0; i < 400; i++)
        {
        	//The nextGaussian method is centered at 0, with a standard
        	//deviation of 1, so its a bell curve spread!
            double x = xo + random.nextGaussian() * 10;
            double y = yo + random.nextGaussian() * 10;
            Tree tree = new Tree(x, y, random.nextInt(12 * Tree.GROW_SPEED), type);

            if(isFree(tree.x, tree.y, tree.radius))
                addEntity(tree);
        }
    }
    
    private void addMinerals(double xo, double yo)
    {
    	for(int i = 0; i < 75; i++)
        {
        	//The nextGaussian method is centered at 0, with a standard
        	//deviation of 1, so its a bell curve spread!
            double x = xo + random.nextGaussian() * 10;
            double y = yo + random.nextGaussian() * 10;
            Mineral mineral = new Mineral(x, y);

            if (isFree(mineral.x, mineral.y, mineral.radius))
                addEntity(mineral);
        }
    }
    
    public boolean isFree(double x, double y, double r)
    {
        return isFree(x, y, r, null);
    }
    //Decision method that utilizes both the isOnGround() and isEmptyLand() methods.
    public boolean isFree(double x, double y, double r, Entity source)
    {
        if (!isOnGround(x, y)) 
        	return false; 
        if (!isEmptyLand(x, y, r, source)) 
        	return false;
        return true;
    }
    
    public boolean isEmptyLand(double x, double y, double r, Entity source)
    {
    	for (int i = 0; i < entities.size(); i++)
        {
            Entity e = entities.get(i);
            if (e != source)
                if (e.collides(x, y, r)) return false;
        }
    	return true;
    }
    
    public boolean isOnGround(double x, double y)
    {
    	///Reconverts the x/y values back into raw/unscaled form.
        x /= 1.5;
        y /= 1.5;
        int xp = (int) (x + 128);
        int yp = (int) (y + 128);
        //If the x or y points are out of range of our pixel image, its obviously
        //out of range.
        if (xp < 0 || yp < 0 || xp >= 256 || yp >= 256) return false;
        //int pixelPosition = (yp * 126) + xp;

        return (pixels[yp << 8 | xp] >>> 24) > 128;
        //return pixels[pixelPosition] > 0;
    }
    
    //DO NOT Directly add to entity list, use this method to ensure images are initialized.
    public void addEntity(Entity entity)
    {
        entity.init(this, images);
        
        if(entity.desc.equals("Tree"))
        	treeCount++;
        else if(entity.desc.equals("Mineral"))
        	mineralCount++;
        else if(entity.desc.equals("Animal"))
        	animalCount++;
        else if(entity.desc.equals("Person"))
        	people.add((Person) entity);
        
        entities.add(entity);
        entity.tick();
    }

    //If the parameter is null, we are returning the entity at the exact x, y, r point, type or source does not matter.
    //If its not null, we are matching the same desctiption (Entity).
    public Entity getEntityAt(double x, double y, double r, Entity theEntity)
    {
        double closest = Integer.MAX_VALUE;
        Entity closestEntity = null;

        for (int i = 0; i < entities.size(); i++)
        {
            Entity entity = entities.get(i); 
            if ((entity.collides(x, y, r) && theEntity == null) || (entity.collides(x, y, r) && entity.desc.equals(theEntity.desc)))
            {
                double dist = (entity.x - x) * (entity.x - x) + (entity.y - y) * (entity.y - y);
                if (dist < closest)
                {
                    closest = dist;
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }
    
	//NOTE* Explicitly not using the exhaust method because it is
	//designed for entity-entity interactions, not for god-entity interactions.
    public void deleteEntityAt(double x, double y, double r)
    {
    	DoublePoint point = this.convertToGameCoords(x, y);
    	Entity target = getEntityAt(point.xCoord, point.yCoord + 5, r, null);
    	
    	if(target != null)
    	{
    		if(target instanceof Pond) {/* Do Nothing, can't delete the pond */}
    		else
    		{
	    		target.alive = false;
	    		//If we are deleting the main hut, also delete it's representation on the island.
	    		if(target instanceof Building)
	    		{
	    			Building isItMainHut = (Building) target;
	    			if(isItMainHut.type.equals("Main Hut"))
	    				this.mainHut = null;
	    		}
    		}
    	}
    }
    
    //Returns the closest type specific resource to the main hut. 
    //HOW TO USE: This is a recursive-able method by design. With many uses.
    //Priority usually is 0, and the listOfRes is a sorted size of 3 list with
    //priorities based on scarce resources. If a res is missing from the island, we move
    //to the next priority!
    public Entity getClosestResourceToHut(String[] listOfRes, int priority)
    {
    	Entity returnedClosest = null;
    	double closestDistance = Integer.MAX_VALUE;
    	
    	String targetRes = listOfRes[priority];
    	if(priority > 0) {System.out.println(priority);}
        
        for(int i = 0; i < entities.size(); i++)
        {	
        	if(entities.get(i).desc.equals(targetRes))    
        	{
        		if(entities.get(i).distance(mainHut) < closestDistance && entities.get(i).targeted == false
        			&& !entities.get(i).impossibleJob)	
        		{
        			returnedClosest = entities.get(i);    
        			closestDistance = returnedClosest.distance(mainHut);
        		}
        	}
        }     
        //The max index is 2, but by then we must return a resource or the island is exhausted.
        if(returnedClosest == null && priority < 2)
        {
        	priority = priority + 1;
        	return getClosestResourceToHut(listOfRes, priority);
        }
        return returnedClosest;
    }   
    //Returns closest resource (Gatherable) of a given type to an inputed person.
	public Entity getClosestEntityTo(Entity src, boolean mustBeGatherable, boolean trueIfSpreadingFire)
	{
		Entity returnedClosest = null;
	  	
	    if(entities.size() < 1)
	        return null;
	      
	    returnedClosest = entities.get(0);
	    
		for(int i = 1; i < entities.size(); i++)
		{	    			
			//Don't gather from resources that are on fire...
		    if(mustBeGatherable && entities.get(i).gatherable && !entities.get(i).desc.equals("Shrine")
		    		&& entities.get(i).distance(src) < returnedClosest.distance(src) && !entities.get(i)
		    		.impossibleJob && entities.get(i).targeted == false && entities.get(i) != src &&
		    		!entities.get(i).onFire && !entities.get(i).untouchable)	
		    {
		    	returnedClosest = entities.get(i); 
		    }
		    else if(trueIfSpreadingFire && entities.get(i).distance(src) < returnedClosest.distance(src)
		    		&& entities.get(i).distance(src) < 200 && entities.get(i).targeted == false &&
		    		entities.get(i) != src && !entities.get(i).onFire && !entities.get(i).untouchable)
		    {
		    	returnedClosest = entities.get(i); 
		    }
		    else if(!mustBeGatherable && !trueIfSpreadingFire && entities.get(i) != src
		    		&& entities.get(i).distance(src) < returnedClosest.distance(src) && !entities.get(i).untouchable)
		    {
		    	returnedClosest = entities.get(i);     
		    }
		}
		
	    //If no entity is close enough, nothing is lit on fire
	    if(returnedClosest == entities.get(0) && trueIfSpreadingFire)
	    	return null;
	    	
	    return returnedClosest;
	 }

     public void supernaturalOnCollided(double x, double y, String desc, Entity src)
     {
    	 for (int i = 0; i < entities.size(); i++)
    	 { 
    	 	 Entity e = entities.get(i);  
    	 	 if(e != src) 
    	 	 {
    			 if(e.collides(x, y, src.radius))
    			 {	
    				 if(desc.equals(Supernatural.METEOR_SHOWER.desc) ||
    						 desc.equals(Supernatural.LIGHTNING.desc))
    				 {
    					 e.burn(); 
    				 }
    				 if(desc.equals(Supernatural.METEOR_SHOWER.desc))
    				 {
    					 e.setNewUpwardVelocity();
    					 e.explodeInAir(); 
    				 }
    				 if(e instanceof Person)
    				 {
    					 Person current = (Person) e;
    					 current.alerted = true;
    				 }
    			 }
    	 	 }
         }
     }
	
	 public DoublePoint convertToGameCoords(double targetX, double targetY)
	 {
		 //Counteracting the scale(1, 0.5).   	
		 targetX *= 0.5;
		 targetY *= -1;
		 //Counteracting the zoom(zoomFactorEntity, zoomFactorEntity). 
		 targetX *= 1 / mainComponent.zoomFactorEntity;
	     targetY *= 1 / mainComponent.zoomFactorEntity;
				 
		 //Rerotating.   	
		 double sin = Math.sin(rot);
		 double cos = Math.cos(rot);
		        
		 double xp = targetX * cos + targetY * sin;
		 double yp = targetX * sin - targetY * cos;
		    	
		 return new DoublePoint(xp, yp);
	 }
	 
	 public int getResourceNumber(String desc)
	 {
		 if(desc.equals("Tree"))
			 return treeCount;
		 else if(desc.equals("Mineral"))
			 return mineralCount;
		 else if(desc.equals("Animal"))
			 return animalCount;
		 //Wont happen..
		 return 0;
	 }
}