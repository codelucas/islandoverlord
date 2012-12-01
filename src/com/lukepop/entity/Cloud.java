package com.lukepop.entity;

import java.awt.Graphics2D;

public class Cloud extends Entity
{
	public static int TOTAL_CLOUD_DURATION = 320;
	public static int CLOUD_DESCENT_TIMER_INTERVAL = 290;
	public static int CLOUD_EVENT_DISSAPPEAR_INTERVAL = 150;
	
	public int cloudTimer = TOTAL_CLOUD_DURATION;
	public int randomCloud = random.nextInt(3);
	public double width, height;
	public double anchorX, anchorY;
	public double elevation;
	public boolean growing = true;
	public boolean shrinking = false;
	
	public Cloud(double x, double y, double anchorX, double anchorY, double elevation) 
	{
		super(x, y, 0, "Cloud");
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.elevation = elevation;
		this.untouchable = true;
	}
	
	public void tick()
	{
		if(cloudTimer > 0)
		{
			if(growing)
			{
				width += 1.6;  //0.8
				height += 0.6; //0.3
			}
			else if (shrinking)
			{
				if(cloudTimer % 4 == 0)
				{
					width -= 0.8;
					height -= 0.3;
				}
			}
			
			if(cloudTimer == CLOUD_DESCENT_TIMER_INTERVAL)
				growing = false;
			else if(cloudTimer == CLOUD_EVENT_DISSAPPEAR_INTERVAL)
				shrinking = true;
			
			cloudTimer--;
		}
		
		if(cloudTimer == 0)
			alive = false;
	}
	
    public void updatePos(double sin, double cos, double alpha)
    {
    	xr = anchorX * cos + anchorY * sin;
    	yr = anchorX * sin - anchorY * cos;
    	yr += elevation;
    }
	
	public void render(Graphics2D g, double alpha)
	{
		int x = (int) (xr - 20);
        int y = -(int) (yr / 2 + 8 * 3 - 4);     
        
        g.drawImage(images.clouds[randomCloud], x, y, (int) width, (int) height, null);
	}
}