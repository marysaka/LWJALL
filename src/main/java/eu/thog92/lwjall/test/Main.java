package eu.thog92.lwjall.test;

import eu.thog92.lwjall.ALSoundProvider;
import eu.thog92.lwjall.ISoundProvider;
import eu.thog92.lwjall.Source;
import org.lwjgl.LWJGLException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Main
{
    public static void main(String[] args) throws LWJGLException, MalformedURLException, InterruptedException
    {
        ISoundProvider soundProvider = new ALSoundProvider();

        URL url = new File("resources/test_mono_8000Hz_8bit_PCM.wav").toURI().toURL(); // file from http://download.wavetlan.com/SVV/Media/HTTP/test_mono_8000Hz_8bit_PCM.wav
        System.out.println("Preparing source");
        Source source = soundProvider.newSource("test", url, "wav", true);
        source.setVolume(0.10F);
        System.out.println("Starting to play");
        soundProvider.play("test");

        while(soundProvider.isPlaying("test"))
            Thread.sleep(10000);
        System.out.println("End of playing");
        soundProvider.cleanUp();
    }
}
