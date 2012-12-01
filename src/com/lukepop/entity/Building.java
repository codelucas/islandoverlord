package com.lukepop.entity;

import java.awt.Graphics2D;

import com.lukepop.island.Expense;

public class Building extends Entity
{
	//Possible types:
		//	Main Hut
		//	Basic Hut (Residence)
		//	Barracks
		//	Library
		//	Pub
		// 	Wonder
	public String type = null;
	
	//The costs will all be static final fields because we need to compute 
	//the cost before creation.
	
	public static final Expense BASIC_HUT_COST = new Expense(250, 0, 0, 0);
	public static final Expense BARRACKS_COST = new Expense(250, 250, 0, 70);
	public static final Expense LIBRARY_COST = new Expense(500, 500, 50, 400);
	public static final Expense MAIN_HUT_COST = new Expense(500, 0, 0, 0);
	public static final Expense PUB_COST = new Expense(150, 150, 150, 0);
	public static final Expense WONDER_COST = new Expense(500, 500, 500, 1000);
	
	//First spot of expense is wood, then stone, then food.
	public Expense expense;
	public int residencyBoost;
	public boolean startedBuilding = false;
	public int totalBuildTime; 
	public int smokeInterval = random.nextInt(100) + 100;
	public int buildTimer = 0;
	public int animFrame = 0;
	public int buildTime = 0;
	
	public int needBuilderIndex; //0: Basic hut, 1: Rax, 2: Library, 3: Wonder
	
	public Building(String type, double x, double y) 
	{
		super(x, y, 0, "Building");
		this.type = type;
		
		if(type.equals("Main Hut"))
		{
			residencyBoost = 0;
			totalBuildTime = 240;
			this.radius = 9;
			expense = MAIN_HUT_COST;
		}
		else if(type.equals("Basic Hut"))
		{
			residencyBoost = 3;
			totalBuildTime = 600;
			this.radius = 4;
			expense = BASIC_HUT_COST;
			needBuilderIndex = 0;
		}
		else if(type.equals("Barracks"))
		{
			residencyBoost = 0;
			totalBuildTime = 1000;
			this.radius = 9;
			expense = BARRACKS_COST;
			needBuilderIndex = 1;
		}
		else if(type.equals("Library"))
		{
			residencyBoost = 0;
			totalBuildTime = 2500;
			this.radius = 13;
			expense = LIBRARY_COST;
			needBuilderIndex = 2;
		}
		else if(type.equals("Pub"))
		{
			residencyBoost = 0;
			totalBuildTime = 750;
			this.radius = 9;
			expense = PUB_COST;
		}
		else if(type.equals("Wonder"))
		{
			residencyBoost = 0;
			totalBuildTime = 4000;
			this.radius = 20;
			expense = WONDER_COST;
			needBuilderIndex = 3;
		}
		workType = "Build";
		//Set the radius to the proper decided radius, 0 in the super
		//constructor was just a place holder. Same with fire factor.
		super.fireLiveCounter = (int) (radius * FIRE_LIVE_FACTOR);
	}
	
	public void tick()
    {
		super.tick();
		
		//Generate smoke for buildings.
		if(buildTimer == totalBuildTime && !type.equals("Basic Hut")) { this.addSmoke(); }
		
		if(totalBuildTime > buildTimer && startedBuilding)
		{
			if(buildTimer % (totalBuildTime / 6) == 0 && buildTimer != 0)
				animFrame++;
			buildTimer++;	
		}
    }
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 8);
	    int y = -(int) (yr / 2 + 16 - 4);
	    
	    if(!startedBuilding)
	    {
	    	//Render nothing, the builder hasen't arrived.
	    }
	    else if(totalBuildTime > buildTimer) 
		{
			 g.drawImage(images.buildings[animFrame], x, y, null);
		}
		else
	    {
		    if(type.equals("Main Hut"))
		    	y -= 8;
		    else if(type.equals("Library"))
		    	x -= 8;
		    else if(type.equals("Wonder"))
		    {
		    	x -= 12;
		    	y -= 8;
		    }
		    
			int completedIndex = 0;
				
			if(type.equals("Main Hut"))
				completedIndex = 6;
			else if(type.equals("Barracks"))
				completedIndex = 7;
			else if(type.equals("Basic Hut"))
				completedIndex = 8;
			else if(type.equals("Pub"))
				completedIndex = 9;
			else if(type.equals("Library"))
				completedIndex = 10;
			else if(type.equals("Wonder"))
				completedIndex = 11;
		    g.drawImage(images.buildings[completedIndex], x, y, null);
	    }
	    
	    if(onFire)
	    	 super.render(g, alpha);
	}
	//NOTE** don't change to else if, keep them all if's.
	public static boolean canAffordType(String type, int currentWood, int currentMinerals, int currentFood, int intelligence)
	{
		if(type.equals("Main Hut")) 
			if(currentWood >= Building.MAIN_HUT_COST.resourceCost[0])
				return true; 
		
		if(type.equals("Basic Hut"))
			if(currentWood >= Building.BASIC_HUT_COST.resourceCost[0])
				return true;
		
		if(type.equals("Barracks"))
			if(currentWood > Building.BARRACKS_COST.resourceCost[0] && currentMinerals >= Building.BARRACKS_COST.resourceCost[1]
					&& intelligence >= BARRACKS_COST.resourceCost[3] && intelligence <= 130)
				return true;
		
		if(type.equals("Library"))
			if(currentWood >= Building.LIBRARY_COST.resourceCost[0] && currentMinerals >= Building.LIBRARY_COST.resourceCost[1]
					&& currentFood >= Building.LIBRARY_COST.resourceCost[2] && intelligence >= LIBRARY_COST.resourceCost[3])
				return true;
		if(type.equals("Wonder"))
			if(currentWood >= Building.WONDER_COST.resourceCost[0] && currentMinerals >= Building.WONDER_COST.resourceCost[1]
					&& currentFood >= Building.WONDER_COST.resourceCost[2] && intelligence >= WONDER_COST.resourceCost[3])
				return true;
		
		return false;  
	}
	
	public void addPropertiesToIsland()
	{
		island.maxPopulation += residencyBoost;
       	island.society.woodCount -= expense.resourceCost[0];
       	island.society.mineralCount -= expense.resourceCost[1];
       	island.society.foodCount -= expense.resourceCost[2];
	}

	public void addSmoke()
	{
		if(island.gameLoop % smokeInterval == 0 && island.gameLoop != 0) 
		{ 
			island.addEntity(new Smoke(x, y + 15)); 
			smokeInterval = random.nextInt(100) + 100;
		}
	}
}