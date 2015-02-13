package eu.thog92.lwjall.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.Source;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class StreamingSource extends Source
{

    private InputStream input;
    private String      type;
    private int         buffers;
    private boolean     eof;
    private boolean gainChange;

    @Override
    public void setup(URL url, String type) throws IOException
    {
        this.type = type;
        try
        {
            channel.setAudioFormat(AudioSystem.getAudioFileFormat(url).getFormat());
        }
        catch(UnsupportedAudioFileException e)
        {
            throw new IOException("Could not read sound file.", e);
        }
        this.input = url.openStream();
        prepareBuffers(type, 2);
    }

    private boolean prepareBuffers(String type, int n) throws IOException
	{
		if(input.available() <= 0) return true;
		for(int i = 0; i < n; i++ )
		{
			buffers++ ;
			byte[] buffer = new byte[48000];
			int length = input.read(buffer);
			if(length == -1)
			{
				input.close();
				return true;
			}
			ByteBuffer audioBuffer = BufferUtils.createByteBuffer(length);
			audioBuffer.put(buffer, 0, length);
			audioBuffer.flip();

			int bufferPointer = AL10.alGenBuffers();
			AL10.alBufferData(bufferPointer, channel.getFormat(), audioBuffer, channel.getSampleRate());

			AL10.alSourceQueueBuffers(channel.getSource(0), bufferPointer);
		}
		return false;
	}

	@Override
	public void update()
	{
        if(gainChange)
        {
            AL10.alSourcef( channel.getSource(0), AL10.AL_GAIN, (getGain() * getVolume()));
            this.gainChange = false;
        }
		int buffersProcessed = AL10.alGetSourcei(channel.getSource(0), AL10.AL_BUFFERS_PROCESSED);
		for(int i = 0; i < buffersProcessed; i++ )
		{
			AL10.alSourceUnqueueBuffers(channel.getSource(0));
		}

		buffers -= buffersProcessed;
		if(buffers == 0 && eof)
		{
			channel.stop();
		}
		if(buffers < 2)
		{
			try
			{
				eof = prepareBuffers(type, 2);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

    @Override
    public void setVolume(float volume)
    {
        super.setVolume(volume);
        this.gainChange = true;
    }

}
