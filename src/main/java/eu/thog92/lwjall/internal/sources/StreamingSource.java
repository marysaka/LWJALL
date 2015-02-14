package eu.thog92.lwjall.internal.sources;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodecManager;
import eu.thog92.lwjall.api.Source;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class StreamingSource extends Source
{

    //
    private String type;

    private boolean eof;
    private boolean gainChange;

    public StreamingSource(ICodecManager codecManager, String sourceName, IChannel channel)
    {
        super(codecManager, sourceName, channel);
    }

    @Override
    public void setup(URL url, String type) throws IOException
    {
        this.type = type;
        this.codec = codecManager.getCodec(type);

        if(codec == null)
        {
            throw new IOException("Codec not found (" + type + ")");
        }
        try
        {
            codec.initialize(url, channel);
            codec.prepareBuffers(2);
        }
        catch(UnsupportedAudioFileException e)
        {
            throw new IOException("Could not read sound file.", e);
        }
    }

    @Override
    public void update()
    {
        if(gainChange)
        {
            AL10.alSourcef(channel.getSource(0), AL10.AL_GAIN, (getGain() * getVolume()));
            this.gainChange = false;
        }
        int buffersProcessed = AL10.alGetSourcei(channel.getSource(0), AL10.AL_BUFFERS_PROCESSED);
        for(int i = 0; i < buffersProcessed; i++)
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
