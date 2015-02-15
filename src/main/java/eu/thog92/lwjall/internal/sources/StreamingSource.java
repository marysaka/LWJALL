package eu.thog92.lwjall.internal.sources;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodecManager;
import eu.thog92.lwjall.api.AbstractSource;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class StreamingSource extends AbstractSource
{

    public StreamingSource(ICodecManager codecManager, String sourceName, IChannel channel)
    {
        super(codecManager, sourceName, channel);
    }

    @Override
    public void setup(URL url, String type) throws IOException
    {
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
        int buffersProcessed = AL10.alGetSourcei(channel.getSource(0), AL10.AL_BUFFERS_PROCESSED);
        codec.update(buffersProcessed);
    }

}
