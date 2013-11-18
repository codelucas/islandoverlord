package com.lukepop.island;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images 
{
	public BufferedImage island, ocean;
	public BufferedImage menuBar, clock, exitDoor;
	
	public BufferedImage[] redTrees, greenTrees, minerals, sheep, monkeys;
	public BufferedImage[][] person;
	public BufferedImage[] carriedStuff; // 0 = wood, 1 = stone, 2 = meat, 3 = godEnergy
	public BufferedImage[] buildings; //0 - 5: Construction, 6: Main , 7: rax, 8: hut, 9: Pub, 10: library, 11: Wonder
	public BufferedImage[] emoticons; // 0 = alert, 1 = build, 2 = sad, 3 = happy, 4 = eating, 5 = bored 6 = Prayer
	public BufferedImage[] meteorite; // 0 =  flying, 1 = large ground, 2 = mid ground, 3 = small ground, 4 = cursor
	public BufferedImage[] clouds;
	public BufferedImage lightning, rainDrop;
	public BufferedImage[] heavyFire, lightFire;
	public BufferedImage[] smoke;
	public BufferedImage[] sciAndReligIcons; //0: Religion, 1: Science
	public BufferedImage[] shrines; //0: Normal, 1: Powered

	public BufferedImage[] speedButtons; //0: faster, 1: slower, 2: fastMouse, 3: slowMouse 
	public BufferedImage[] volumeButtons;  //0: pause 1: play
	public BufferedImage[] godPowerButtons; //0: rain, 1: thunder, 2: meteroites, 3: erase 		
	public BufferedImage[] checkAndEx; //0: check, 1: x mark
	
	public BufferedImage[] logoSequence; //0: Island logo, 1: Overlord logo, 2-6 : play, sandbox, demolition, instructions, credits 
	public BufferedImage snowflake;
	
	public void loadAll() throws IOException
	{
		island = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[256*256];
        ImageIO.read(Images.class.getResource("/rawIsland.png")).getRGB(0, 0, 256, 256, pixels, 0, 256);
        island.setRGB(0, 0, 256, 256, pixels, 0, 256);
        
        ocean = ImageIO.read(Images.class.getResource("/ocean.png"));
        menuBar = ImageIO.read(Images.class.getResource("/menuBar.png"));
        clock = ImageIO.read(Images.class.getResource("/clock.png"));
        
        BufferedImage src = ImageIO.read(Images.class.getResource("/sheet.png"));
        
        //Load the first 16 trees (Apple)
        redTrees = new BufferedImage[12]; greenTrees = new BufferedImage[12];
        for (int i = 0; i < 12; i++)
        	redTrees[i] = clip(src, i * 8, 0, 8, 16);
        for(int j = 0; j < 12; j++)
        	greenTrees[j] = clip(src, j * 8, 48, 8, 16);
        
        person = new BufferedImage[2][12];
        for(int i = 0; i < 2; i++)
        	for(int j = 0; j < 12; j++)
        		person[i][j] = clip(src, j * 8, 16 + 8 * i, 8, 8);
       
        carriedStuff = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	carriedStuff[i] = clip(src, (12 * 8) + i * 8, 4 * 8, 8, 8);
        
        minerals = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	minerals[i] = clip(src, 96 + i * 8, 16, 8, 8);
        
        sheep = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	sheep[i] = clip(src, i * 8, 64, 8, 8);
     
        monkeys = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	monkeys[i] = clip(src, i * 16, 9 * 8, 16, 16);
        
        emoticons = new BufferedImage[7];
        for(int i = 0; i < 7; i++)
        	emoticons[i] = clip(src, i * 8, 4 * 8, 8, 8);
        
        buildings = new BufferedImage[12];
        //Add under construction buildings.
        for(int i = 0; i < 6; i++)
        	buildings[i] = clip(src, 16 * 8, i * 16, 16, 16);   
        
        buildings[6] = clip(src, 18 * 8, 0, 16, 24);  //Main building       
        buildings[7] = clip(src, 18 * 8, 24, 16, 16); //Barracks        
        buildings[8] = clip(src, 18 * 8, 24 + 16, 16, 16); //Basic Hut       
        buildings[9] = clip(src, 18 * 8, 24 + (16 * 2), 16, 16); //Pub      
        buildings[10] = clip(src, 18 * 8, 24 + (16 * 3), 24, 16); //Library
        buildings[11] = clip(src, 21 * 8, 32 + (16 * 2), 40, 24); //Wonder
        
       meteorite = new BufferedImage[5];
        for(int i = 0; i < 4; i++)
        	meteorite[i] = clip(src, i * 24, 12 * 8, 24, 24);
        
        meteorite[4] = clip(src, 4 * 24, 12 * 8, 8 * 7, 24);
        
        clouds = new BufferedImage[3];
        for(int i = 0; i < 3; i++)
        	clouds[i] = clip(src, i * (5 * 8), 15 * 8, 5 * 8, 3 * 8);
        
        lightning = clip(src, 12 * 8, 5 * 8, 16, 7 * 8); 
        rainDrop = clip(src, 14 * 8, 5 * 8, 16, 16); 
        
        heavyFire = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	heavyFire[i] = clip(src, (15 * 8) + (16 * i), 16 * 8, 2 * 8, 3 * 8);
        
        lightFire = new BufferedImage[9];
        for(int i = 0; i < 9; i++)
        	lightFire[i] = clip(src, (3 * 8) + (8 * i), 19 * 8, 8, 16);
        
        smoke = new BufferedImage[4];
        for(int i = 3; i >= 0; i--)
        	smoke[3 - i] = clip(src, 24 * 8, (i * 8), 8, 8);
        
        volumeButtons = new BufferedImage[2];
        volumeButtons[0] = clip(src, 21 * 8, 2 * 8, 16, 16);
        volumeButtons[1] = clip(src, 21 * 8, 4 * 8, 16, 16);
        
        godPowerButtons = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	godPowerButtons[i] = clip(src, 0, (18 * 8) + (i * 16), 16, 16);
        
        speedButtons = new BufferedImage[4];
        for(int i = 0; i < 4; i++)
        	speedButtons[i] = clip(src, (23 * 8) + (8 * i),  4 * 8, 8, 8);
        
        checkAndEx = new BufferedImage[2];
        checkAndEx[0] = clip(src, 20 * 8, 0, 8, 8);  checkAndEx[1] = clip(src, 21 * 8, 0, 8, 8);
        
        sciAndReligIcons = new BufferedImage[2];
        sciAndReligIcons[0] = clip(src, 23 * 8, 5 * 8, 8, 16);
        sciAndReligIcons[1] = clip(src, 24 * 8, 5 * 8, 16, 16);
        
        shrines = new BufferedImage[2];
        shrines[0] = clip(src, 19 * 8, 11 * 8, 16, 8 * 4);
        shrines[1] = clip(src, 21 * 8, 11 * 8, 16, 8 * 4);
        
        logoSequence = new BufferedImage[7];
        logoSequence[0] = clip(src, 4 * 8, 21 * 8, 8 * 9, 8 * 3);
        logoSequence[1] = clip(src, 3 * 8, 24 * 8, 8 * 10, 8 * 4);
        for(int i = 2; i < 7; i++)
        	logoSequence[i] = clip(src, 13 * 8, (19 * 8) + ((i - 2) * 8), 16, 8);
        
        snowflake = clip(src, 21 * 8, 19 * 8, 8, 8);
        exitDoor = clip(src, 25 * 8, 0, 16, 3 * 8);
	}
	
	public static BufferedImage clip(BufferedImage src, int x, int y, int w, int h)
    {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        BufferedImage newImage = null;

        try
        {
            GraphicsDevice screen = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = screen.getDefaultConfiguration();
            newImage = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        }
        catch (Exception e){}

        if(newImage == null)
        {
            newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        int[] pixels = new int[w * h];
        src.getRGB(x, y, w, h, pixels, 0, w);
        newImage.setRGB(0, 0, w, h, pixels, 0, w);

        return newImage;
    }
}
