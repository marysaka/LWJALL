package eu.thog92.lwjall.tests;

import java.net.URL;

import eu.thog92.lwjall.ALSoundProvider;
import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.api.ISoundProvider;

public class Main implements Runnable
{
    public static void main(String[] args)
    {
        new Thread(new Main()).start();
    }

    @Override
    public void run()
    {
        try
        {
            ISoundProvider soundProvider = new ALSoundProvider();
            URL url = Main.class.getResource("/sounds/test.wav").toURI().toURL(); // file from http://download.wavetlan.com/SVV/Media/HTTP/test_mono_8000Hz_8bit_PCM.wav
            //            URL url = new File("resources/test.ogg").toURI().toURL();
            System.out.println("Preparing source");
            AbstractSource source = soundProvider.newSource("test", url, true);
            soundProvider.setMasterGain(0.90F);
            System.out.println("Starting to play");
            soundProvider.play("test");

            float frequency = 220.f;
            float period = 1.f / frequency;
            long periodInMilli = (long)(period * 1000);
            long startTime = System.currentTimeMillis();
            while(soundProvider.isPlaying("test"))
            {
                long elapsed = System.currentTimeMillis() - startTime;
                if(elapsed >= periodInMilli)
                {
                    for(int i = 0; i < elapsed / periodInMilli; i++ ) // If we skip a frame, run it
                    {
                        soundProvider.update();
                    }
                    startTime = System.currentTimeMillis();
                }
            }
            System.out.println("End of playing");
            soundProvider.cleanup();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
