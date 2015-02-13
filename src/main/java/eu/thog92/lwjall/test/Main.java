package eu.thog92.lwjall.test;

import java.net.MalformedURLException;
import java.net.URL;

import eu.thog92.lwjall.ALSoundProvider;

import org.lwjgl.LWJGLException;

public class Main
{
	public static void main(String[] args) throws LWJGLException, MalformedURLException
	{
		ALSoundProvider soundProvider = new ALSoundProvider();

		URL url = Main.class.getResource("/test_mono_8000Hz_8bit_PCM.wav"); // file from http://download.wavetlan.com/SVV/Media/HTTP/test_mono_8000Hz_8bit_PCM.wav
		System.out.println("Preparing source");
		soundProvider.newSource("test", url, "wav", true);
		System.out.println("Starting to play");
		soundProvider.play("test");

		while(soundProvider.isPlaying("test"))
			;
		System.out.println("End of playing");
		soundProvider.cleanUp();
	}
}
