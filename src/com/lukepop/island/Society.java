package com.lukepop.island;

import java.util.ArrayList;

public class Society 
{
    //Resources/Variables: (Increments of 10)
    public int foodCount, mineralCount;
    public int woodCount = 500;
    public int intelligence;
    public ArrayList<Tuple> resources = new ArrayList<Tuple>();
    
    public Island island;
    
    public Society(Island island)
    {
    	this.island = island;
    }
    
    //"M" Minerals, "F" Food, "W" Wood, "N" nothing.
    public void addResources(String oneCharacter)
    {
    	if(oneCharacter.equals("M"))
    		mineralCount += 10;
    	else if(oneCharacter.equals("F"))
    		foodCount += 10;
    	else if(oneCharacter.equals("W"))
    		woodCount += 10;
    	else if(oneCharacter.equals("E"))
    		island.mainComponent.powerHandler.godEnergy += 250;
    }
    
    //Returns String representing scarce resource.
    public String[] getResPriorityList()
    {
    	String[] returnList = new String[3];
    	
    	int inflatedWoodCount = woodCount - 500;
    	resources.add(new Tuple("Tree", inflatedWoodCount));
    	resources.add(new Tuple("Animal", foodCount));
    	resources.add(new Tuple("Mineral", mineralCount));

    	Tuple first = resources.get(0);
    	Tuple second, third;
    	
    	//Pick the biggest out of 3.
    	for(int i = 1; i < resources.size(); i++)
    		if(resources.get(i).amount < first.amount && island.getResourceNumber(resources.get(i).resType) > 0)
    			first = resources.get(i);	
    	
    	resources.remove(first);
    	
    	//Biggest out of 2.
    	if(resources.get(0).amount >= resources.get(1).amount) { second = resources.get(0); }
    	else { second = resources.get(1); }
    	
    	resources.remove(second);
    	
    	//Remaining third.
    	third = resources.get(0);
    	
    	//Add in order.
    	returnList[0] = first.resType;
    	returnList[1] = second.resType;
    	returnList[2] = third.resType;
    	
    	resources.clear();
    	return returnList;
    }
}

//Quick class just for wrapping resources for comparison.
class Tuple
{
	String resType;
	int amount;
	
	public Tuple(String resType, int amount)
	{
		this.resType = resType;
		this.amount = amount;
	}
}
