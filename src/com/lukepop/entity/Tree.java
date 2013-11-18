package com.lukepop.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Tree extends Entity
{
	public static final int GROW_SPEED = 800;  // Higher the slower the growth
	public static final int SPREAD_INTERVAL = 15000; //default 20,000 - 30,000
	public static final int WORK_TIMER = 50;
	int age, spreadDelay;
    int stamina = 0;
    //Apple or Palm Tree for now.
    //The age range is from 0 to 11, corresponding to the sprite sheet index!
    public String type; 
	
	public Tree(double x, double y, int age, String type)
    {
        super(x, y, 4, "Tree");
        this.stamina = this.age = age;
        spreadDelay = random.nextInt(SPREAD_INTERVAL); 
        this.type = type;
        this.workType = "Woodcut";
        gatherable = true;
    }
	
	public void tick()
    {
		super.tick();
		//Increment until the growth speed increases.
        if (age < 11 * GROW_SPEED)
        {
            age++;
            stamina++;
        }
        else if (spreadDelay <= 0)
        {
            double xp = x + random.nextGaussian() * 8;
            double yp = y + random.nextGaussian() * 8;
            //Generate children of the same type.
            Tree tree = new Tree(xp, yp, 0, type);

            if (island.isFree(tree.x, tree.y, tree.radius)) island.addEntity(tree);

            spreadDelay = SPREAD_INTERVAL;
        }
        spreadDelay--;
    }
	
	 public void render(Graphics2D g, double alpha)
	 {
	     int x = (int) (xr - 4);
	     int y = -(int) (yr / 2 + 16);
	 
	     if(type.equals("Apple"))
	    	 g.drawImage(images.redTrees[11 - age / GROW_SPEED], x, y, null);
	     else
	    	 g.drawImage(images.greenTrees[11 - age / GROW_SPEED], x, y, null); 
	     
	     if(onFire)
	    	 super.render(g, alpha);	
	     
																	     if(impossibleJob)
																	     {
																	    	g.setColor(Color.BLACK);
																	    	g.fill(new Rectangle(x, y, 5, 5));
																	     }
	 }
}
