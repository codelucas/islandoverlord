package com.lukepop.island;

import java.awt.Cursor;
import java.awt.Graphics2D;

import com.lukepop.game.MainComponent;
import com.lukepop.supernatural.Supernatural;

public class MenuHandler 
{
	public static void handleMenuClicks(double xMouse, double yMouse, Supernatural powerHandler, MainComponent component)
	{
		//Closing and opening the menu code.
		if(xMouse >= 80 && xMouse <= 90 && yMouse <= 342 && yMouse >= 280 &&
				(component.menuStatus.equals("Open") || component.menuStatus.equals("None"))) 	
			component.menuStatus = "Closed";
		else if(xMouse >= 0 && xMouse <= 9 && yMouse >= 280 && yMouse <= 342 &&
				(component.menuStatus.equals("Closed") || component.menuStatus.equals("None")))
			component.menuStatus = "Open";
		
		//Code for speeding/slowing down the time (buttons).
		if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 0 &&
				xMouse <= 30 && yMouse >= 40 && yMouse <= 75)
		{
			Sound.play(Sound.TAP, 0);
			MainComponent.gameSpeed--;
			if(MainComponent.gameSpeed < 0)
				MainComponent.gameSpeed = 0;
		}
		//Code for speeding/slowing down the time (buttons).
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 30 &&
				xMouse <= 80 && yMouse >= 40 && yMouse <= 75)
		{
			Sound.play(Sound.TAP, 0);
			MainComponent.gameSpeed++;
			if(MainComponent.gameSpeed > 4)
				MainComponent.gameSpeed = 4;
		}
		//Meteor button toggle:
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 5 &&
				xMouse <= 80 && yMouse >= 85 && yMouse <= 145)
		{	
			if(powerHandler.currentPower.equals("Meteor Shower"))
			{
				powerHandler.switchTo("None");
			}
			else
			{
				powerHandler.switchTo("Meteor Shower");
				component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
		//Lightning button toggle:
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse >= 160 && yMouse <= 220)
		{
			if(powerHandler.currentPower.equals("Lightning"))
			{
				powerHandler.switchTo("None");
			}
			else
			{
				powerHandler.switchTo("Lightning");
				component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
		//Rain button toggle:
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse >= 230 && yMouse <= 290)
		{
			if(powerHandler.currentPower.equals("Rain"))
			{
				powerHandler.switchTo("None");
			}
			else
			{
				powerHandler.switchTo("Rain");
				component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
		//Erase button toggle:
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 5 && 
				xMouse <= 80 && yMouse <= (component.HEIGHT * 2) - 70 && yMouse >= (component.HEIGHT * 2) - 125)
		{
			if(powerHandler.currentPower.equals("Erase"))
			{
				powerHandler.switchTo("None");
			}
			else
			{
				powerHandler.switchTo("Erase");
				component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
		//Music button toggle: 
		else if((component.menuStatus.equals("Open") || component.menuStatus.equals("None")) && xMouse >= 15 &&
				xMouse <= 60 && yMouse >= (component.HEIGHT * 2) - 50 && yMouse <= (component.HEIGHT * 2) - 10)
		{
			Sound.play(Sound.TAP, 0);
			component.musicOn = !component.musicOn;
		}		
		//Quit button toggle:
		else if(xMouse >= (component.WIDTH * 2 - 18 * 2) && xMouse <= (component.WIDTH * 2) && 
				yMouse >= 0 && yMouse <= 24 * 2)
		{
			Sound.play(Sound.TAP, 0);
			MainComponent.layout.swapView("Start");
		}		
	}
	
	public static void renderSideBar(Graphics2D clone)
	{
		//TODO: Later...
	}
}
