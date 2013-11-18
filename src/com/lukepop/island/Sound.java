package com.lukepop.island;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;


public class Sound 
{
    public static final Clip COMET_FLYING = loadClip("/sound/cometFlying.wav");
    public static final Clip COMET_IMPACT = loadClip("/sound/cometImpact.wav");
    public static final Clip TAP = loadClip("/sound/tap.wav");
    public static final Clip THUNDER = loadClip("/sound/thunder.wav");

    private static Clip loadClip(String resourceName)
    {
        try
        {
            AudioInputStream sound = AudioSystem.getAudioInputStream(Sound.class.getResource(resourceName));

            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
            Clip clip = (Clip) AudioSystem.getLine(info);
            clip.open(sound);

            return clip;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void play(Clip clip, int x)
    {
        if (clip!=null)
        {
            try
            {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
