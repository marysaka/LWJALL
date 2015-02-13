package eu.thog92.lwjall.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.ICodecManager;
import eu.thog92.lwjall.Source;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class StreamingSource extends Source
{

    //
    private String      type;

    private boolean     eof;
    private boolean gainChange;

    public StreamingSource(ICodecManager codecManager)
    {
        super(codecManager);
    }

    @Override
    public void setup(URL url, String type) throws IOException
    {
        this.type = type;
        this.codec = codecManager.getCodec(type);

        if(codec == null)
            throw new IOException("Codec not found (" + type + ")");
        try
        {
            AudioFormat format = AudioSystem.getAudioFileFormat(url).getFormat();
            channel.setAudioFormat(format);
            codec.setAudioFormat(format);
        }
        catch(UnsupportedAudioFileException e)
        {
            throw new IOException("Could not read sound file.", e);
        }
        codec.initialize(url, channel);
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
        codec.update(buffersProcessed);
	}

    @Override
    public void setVolume(float volume)
    {
        super.setVolume(volume);
        this.gainChange = true;
    }

}
