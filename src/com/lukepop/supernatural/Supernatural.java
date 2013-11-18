package com.lukepop.supernatural;

import java.awt.Cursor;

import com.lukepop.entity.Lightning;
import com.lukepop.entity.Meteor;
import com.lukepop.entity.Rain;
import com.lukepop.game.MainComponent;
import com.lukepop.island.Expense;
import com.lukepop.island.Sound;

public class Supernatural 
{
	//Possible powers:
	public static final GodPower METEOR_SHOWER = new GodPower("Meteor Shower", new Expense(500));
	public static final GodPower ERASE = new GodPower("Erase", new Expense(700));
	public static final GodPower LIGHTNING = new GodPower("Lightning", new Expense(350));
	public static final GodPower RAIN = new GodPower("Rain", new Expense(0));
	public static final GodPower NONE = new GodPower("None", new Expense(0));
	
	//Amount of "money" for the player, who is playing as a diety.
	public int godEnergy = 1000;
	//For illigal actions, display something for an amount of "time".
	public static final int INDICATOR_COUNTER = 50;
	
	public String currentPower = "None";
	public int powerRadius;
	public MainComponent component;
	
	//Shake counters for meteors:
	public boolean powerHasHit = false;
	public String shakeDirection = "Left";
	//NOTE* THe directionDuration and shakeCounter must always be divisible because 
	//the directionDuration splits the shakeCounter into peices, where its shaking.
	//When the shaking stops, we want it to be in the same place it was!
	public int directionDuration, directionCounter, totalShakeTime;
	
	public Supernatural(MainComponent component)
	{
		this.component = component;
	}
	
	public void switchTo(String power)
	{
		component.shakeFactor = 0;
		
		if(canAfford(power) || component.superMode)
		{
			Sound.play(Sound.TAP, 0);
			this.currentPower = power;
		}
		else
		{
			this.currentPower = "None";
			component.illigalFlagTimer = INDICATOR_COUNTER;
		}
	}
	
	public void payForPower(String power)
	{
		godEnergy -= returnCostOfPower(power);
	}
	
	public void launchPower(double translatedX, double translatedY, double randomXSpawn)
	{
	    //Supernatural entities (meteors, lightning, etc), require this component to
	    //be passed in the constructor because island's add entity only initializes an island
	    //within the object AFTER the object is intialzied, but these supernaturals require
	    //the island object WITHIN their constructor.
		
		if(!canAfford(currentPower) && !component.superMode)
		{
			currentPower = "None";
			component.illigalFlagTimer = INDICATOR_COUNTER;
			return;
		}
		
		if(!component.superMode)
			this.payForPower(currentPower);
		
	    if(currentPower.equals("Meteor Shower"))
	    {
	    	component.island.addEntity(new Meteor(randomXSpawn, -600, translatedX, translatedY, component)); 
	    }
	    if(currentPower.equals("Lightning"))
	    {
	    	component.island.addEntity(new Lightning(translatedX, translatedY, component)); 
	    }
	    if(currentPower.equals("Rain"))
	    {
	    	component.island.addEntity(new Rain(translatedX, translatedY, component)); 
	    }
	    if(currentPower.equals("Erase"))
	    {
	    	component.island.deleteEntityAt(translatedX, translatedY, 10);
	    }
	}
	
	public void tick()
	{
		if(currentPower.equals("None"))
			component.setCursor(Cursor.getDefaultCursor());
		
		if(currentPower.equals(METEOR_SHOWER.desc))
		{
			 directionDuration = 5;
			
			 if(powerHasHit)
		     {
				//Right when the meteor hits, kickstart the shake counter.
				 totalShakeTime = 40; 
				 powerHasHit = false;
		     }

		     if(totalShakeTime > 0)
		     {
		          if(shakeDirection.equals("Left"))
		          {
		        		component.shakeFactor = 3;							
		        		if(directionCounter >= directionDuration)
		        		{						
		        			shakeDirection = "Right";
		        			directionCounter = 0;
		        		}
		        		directionCounter++;
		        	}
		        	else if (shakeDirection.equals("Right"))
		        	{
		        		component.shakeFactor = -3;								
		        		if(directionCounter >= directionDuration)
		        		{						
		        			shakeDirection = "Left";
		        			directionCounter = 0;
		        		}
		        		directionCounter++;
		        	}
		        	totalShakeTime--;
		     }
		     else
		     {
		    	 component.shakeFactor = 0;
		     }
		}
	}
	
	public boolean canAfford(String power)
	{
		if(power.equals(METEOR_SHOWER.desc) && godEnergy >= METEOR_SHOWER.expense.supernatCost[0])
			return true;
		
		else if(power.equals(ERASE.desc) && godEnergy >= ERASE.expense.supernatCost[0])
			return true;
		
		else if(power.equals(LIGHTNING.desc) && godEnergy >= LIGHTNING.expense.supernatCost[0])
			return true;
		
		else if(power.equals(RAIN.desc) && godEnergy >= RAIN.expense.supernatCost[0])	//If its RAIN
			return true;
		
		else if(power.equals(NONE.desc))
			return true;
		
		return false;
	}
	
	public int returnCostOfPower(String power)
	{
		if(power.equals(METEOR_SHOWER.desc))
			return METEOR_SHOWER.expense.supernatCost[0];
		else if(power.equals(ERASE.desc))
			return ERASE.expense.supernatCost[0];
		else if(power.equals(LIGHTNING.desc))
			return LIGHTNING.expense.supernatCost[0];
		else if(power.equals(RAIN.desc))
			return RAIN.expense.supernatCost[0];
		else if(power.equals(NONE.desc))
			return NONE.expense.supernatCost[0];
		
		return Integer.MAX_VALUE;
	}
}