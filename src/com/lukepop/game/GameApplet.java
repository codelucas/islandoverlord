package com.lukepop.game;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JApplet;
import javax.swing.JPanel;

import com.lukepop.island.Images;

public class GameApplet extends JApplet
{
	private static final long serialVersionUID = 1L;
	private Layout switcher;
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 640;
	
	public void init()
    {	
		switcher = new Layout();
		this.setSize(WIDTH, HEIGHT);
		this.setLayout(new BorderLayout());
		this.setContentPane(switcher);
    }
	    
	public void start()
	{	
		switcher.startMenu.start();
	}
	
    public void stop()
    {
    	//Called before destroy() when the applet is closed.
    }

    public void destroy()
    {
    	switcher.startMenu.stop();
    	switcher.defaultGame.stop();
    }
    
    public class Layout extends JPanel
    {
		private static final long serialVersionUID = 1L;
		
		public Sequencer sequencer;
		public Sequence TO_ZANARKAND;
		public Images images = new Images();
		
		final StartMenu startMenu;
    	final MainComponent defaultGame;
    	
    	private CardLayout myLayout = new CardLayout();
    	private String previousScreen = "Start";
    	
	    public Layout()
	    { 	
	    	try { images.loadAll(); } catch (IOException e1) {}
	    	
	    	startMenu = new StartMenu(this, GameApplet.WIDTH/2, GameApplet.HEIGHT/2);
	    	defaultGame = new MainComponent(this, GameApplet.WIDTH/2, GameApplet.HEIGHT/2);
	    	
	        //Set the layout and add the menus. Assign them names 
	        setLayout(myLayout);
	        add(startMenu, "Start"); 
	        add(defaultGame, "DefaultGame");
	        try 
		    {
	    		//Load the sequence
	    		TO_ZANARKAND = MidiSystem.getSequence(MainComponent.class.getResource("/sound/ToZanarkand.mid"));
		        //Create a sequencer for the sequence
		        sequencer = MidiSystem.getSequencer();
		        sequencer.open();	
		    } 
	        catch (MalformedURLException e) {} 
		    catch (IOException n) {} 
		    catch (MidiUnavailableException k) {} 
		    catch (InvalidMidiDataException p) {}
	        this.setBackground(Color.BLACK);
	        this.setSize(GameApplet.WIDTH, GameApplet.HEIGHT);
	    	setVisible(true);  
	    }
	    
	    public void swapView(String name)
	    {
	    	//This sequence is required because we don't have completly different
	    	//componenets for sandbox and demolition, because they are so similar.
	    	String converted = name;
	    	if(name.equals("SandBoxGame")) { converted = "DefaultGame"; }
	    	myLayout.show(getContentPane(), converted);
	    	
	    	if(name.equals("DefaultGame") || name.equals("SandBoxGame"))
		    { 
	    		startMenu.stop(); 
		        try 
		        {
					sequencer.setSequence(TO_ZANARKAND);
					sequencer.setTickPosition(sequencer.getLoopStartPoint());
					sequencer.start();
				} 
		        catch (InvalidMidiDataException e) {} 
		        
		        sequencer.setLoopCount(Integer.MAX_VALUE);    
		        defaultGame.requestFocusInWindow();
		        
		        if(name.equals("DefaultGame")) {  defaultGame.start(false); }
		        else if(name.equals("SandBoxGame")) {  defaultGame.start(true); }
		    }
		    else if(name.equals("Start"))
		    {        
		       if(previousScreen.equals("DefaultGame") || previousScreen.equals("SandBoxGame"))
		       { 
		    	   defaultGame.stop();
		    	   //This MUST go after stopping the component!
		    	   sequencer.stop(); 
		       }
		       startMenu.requestFocusInWindow();
		       startMenu.start();		 
		    }    
	    	previousScreen = name;
	    }
    }
}
