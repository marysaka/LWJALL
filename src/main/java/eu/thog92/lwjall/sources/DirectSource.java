package eu.thog92.lwjall.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.Source;
import eu.thog92.lwjall.util.Buffers;

import org.lwjgl.LWJGLException;

public class DirectSource extends Source
{

	@Override
	public void setup(URL url, String type) throws IOException, LWJGLException
	{
		InputStream stream = url.openStream();
		AudioFormat audioFormat = null;
		try
		{
			AudioInputStream ain = AudioSystem.getAudioInputStream(url);
			audioFormat = ain.getFormat();
		}
		catch(UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
		ByteBuffer buffer = Buffers.consumeStream(stream);
		stream.close();
		channel.setup(audioFormat, buffer);
	}

}
