package eu.thog92.lwjall;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.sound.sampled.AudioFormat;

import eu.thog92.lwjall.util.Buffers;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

// TODO: Streaming

/**
 *
 */
public class NormalChannel implements IChannel
{
	/**
	 * OpenAL's IntBuffer identifier for this channel.
	 */
	private IntBuffer source;

	/**
	 * The sample rate for this channel
	 */
	private int	   sampleRate;

	/**
	 * OpenAL data format to use when playing back the assigned source.
	 */
	private int	   format;

	private int	   bufferPointer;

	public NormalChannel(IntBuffer source)
	{
		this.source = source;
	}

	@Override
	public int getSampleRate()
	{
		return sampleRate;
	}

	@Override
	public void setSampleRate(int sampleRate)
	{
		this.sampleRate = sampleRate;
	}

	@Override
	public int getSource(int index)
	{
		return source.get(index);
	}

	@Override
	public void cleanup()
	{
		if(source != null)
		{
			// Stop the Sound
			AL10.alSourceStop(source);

			// Delete OpenAL Source
			AL10.alDeleteSources(source);
			source.clear();
			source = null;
		}
		else
		{
			System.err.println("Attending to cleanup a Source with a empty buffer");
		}
	}

	@Override
	public void setAudioFormat(AudioFormat audioFormat)
	{
		int soundFormat = 0;
		if(audioFormat.getChannels() == 1)
		{
			if(audioFormat.getSampleSizeInBits() == 8)
			{
				soundFormat = AL10.AL_FORMAT_MONO8;
			}
			else if(audioFormat.getSampleSizeInBits() == 16)
			{
				soundFormat = AL10.AL_FORMAT_MONO16;
			}
			else
			{
				System.err.println("Illegal sample size in method " + "'setAudioFormat'");
				return;
			}
		}
		else if(audioFormat.getChannels() == 2)
		{
			if(audioFormat.getSampleSizeInBits() == 8)
			{
				soundFormat = AL10.AL_FORMAT_STEREO8;
			}
			else if(audioFormat.getSampleSizeInBits() == 16)
			{
				soundFormat = AL10.AL_FORMAT_STEREO16;
			}
			else
			{
				System.err.println("Illegal sample size in method " + "'setAudioFormat'");
				return;
			}
		}
		else
		{
			System.err.println("Audio data neither mono nor stereo in " + "method 'setAudioFormat'");
			return;
		}
		format = soundFormat;
		sampleRate = (int)audioFormat.getSampleRate();
	}

	@Override
	public void play()
	{
		AL10.alSourcePlay(source.get(0));
	}

	@Override
	public void pause()
	{
		AL10.alSourcePause(source.get(0));
	}

	@Override
	public void stop()
	{
		AL10.alSourceStop(source.get(0));
	}

	@Override
	public void rewind()
	{
		AL10.alSourceRewind(source.get(0));
	}

	@Override
	public boolean isPlaying()
	{
		return AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	@Override
	public float getPlayingDuration()
	{
		float bytesPerFrame = 1f;
		switch(format)
		{
			case AL10.AL_FORMAT_MONO8:
				bytesPerFrame = 1f;
				break;
			case AL10.AL_FORMAT_MONO16:
				bytesPerFrame = 2f;
				break;
			case AL10.AL_FORMAT_STEREO8:
				bytesPerFrame = 2f;
				break;
			case AL10.AL_FORMAT_STEREO16:
				bytesPerFrame = 4f;
				break;
			default:
				break;
		}
		return (((float)AL10.alGetSourcei(source.get(0), AL11.AL_BYTE_OFFSET) / bytesPerFrame) / (float)sampleRate) * 1000;
	}

	@Override
	public void setup(URL url) throws IOException
	{
		String type = url.getFile().substring(url.getFile().lastIndexOf("."));
		setup(url, type);
	}

	@Override
	public void setup(URL url, String type) throws IOException
	{
		InputStream stream = url.openStream();
		ByteBuffer buffer = Buffers.consumeStream(stream);
		stream.close();
		bufferPointer = AL10.alGenBuffers();
		int error = AL10.alGetError();
		if(error != AL10.AL_NO_ERROR) throw new IOException("Error while creating sound buffer: " + error);
		AL10.alBufferData(bufferPointer, format, buffer, sampleRate);
	}
}
