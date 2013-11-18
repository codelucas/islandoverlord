package com.lukepop.entity;

import java.awt.Graphics2D;

public class Smoke extends Entity
{
    public double xa, ya, za;
    public double z = 0;
	public int durationCounter;
	public int totalDuration;
	public double vertSpeed;
	
	public Smoke(double x, double y) 
	{
		super(x, y, 0, "Smoke");
		this.totalDuration = random.nextInt(50) + 100;
        this.z = random.nextInt(15);
        this.za = 0.33;
        this.vertSpeed = 0.01 + (random.nextInt(50) / 100);
	}
    
    public void tick()
    {
        xa *= 0.99;
        ya *= 0.99;
        za *= 0.99;
        za += vertSpeed;
        if(Math.random() < 0.5) { xa += 0.008; }
        else { xa -= 0.008; }
        x += xa;
        y += ya;
        z += za;
        
        if (durationCounter++ == totalDuration) { alive = false; }
    }

    public void render(Graphics2D g, double alpha)
    {
        int x = (int) (xr - 4);
        int y = -(int) (yr / 2 + 4 + (z + za));
        
        int age = durationCounter * 6 / totalDuration;
        if(age < 4)
        {
        	g.drawImage(images.smoke[age], x, y, 16, 16, null);
        }
    }
}
