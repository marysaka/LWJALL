package eu.thog92.lwjall.internal.sources;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodecManager;
import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.api.ISoundProvider;
import eu.thog92.lwjall.util.LWJALLException;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public class StreamingSource extends AbstractSource
{

    public StreamingSource(ISoundProvider soundProvider, String sourceName, IChannel channel) throws LWJALLException {
        super(soundProvider, sourceName, channel);
    }

    @Override
    public void setup(URL url, String type) throws LWJALLException {
        this.codec = codecManager.getCodec(type);

        if(codec == null)
        {
            throw new LWJALLException("Codec not found (" + type + ")");
        }
        try
        {
            codec.initialize(url, channel);
            codec.prepareBuffers(2);
        }
        catch(Exception e)
        {
            throw new LWJALLException("Could not read sound file", e);
        }
    }

    @Override
    public void update() throws LWJALLException {
        int buffersProcessed = AL10.alGetSourcei(channel.getSource(0), AL10.AL_BUFFERS_PROCESSED);
        codec.update(buffersProcessed);
    }

}
