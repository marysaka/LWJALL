package eu.thog92.lwjall.internal.sources;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodecManager;

import eu.thog92.lwjall.api.ISoundProvider;
import eu.thog92.lwjall.util.LWJALLException;
import org.lwjgl.openal.AL10;

public class DirectSource extends AbstractSource
{

    public DirectSource(ISoundProvider soundProvider, String sourceName, IChannel channel) throws LWJALLException {
        super(soundProvider, sourceName, channel);
    }

    @Override
    public void setup(URL url, String type) throws LWJALLException
    {
        this.codec = codecManager.getCodec(type);
        try
        {
            codec.initialize(url, channel);
        }
        catch(Exception e)
        {
            throw new LWJALLException("Invalid file: " + url.toExternalForm(), e);
        }
        AudioFormat audioFormat = codec.getAudioFormat();
        channel.setup(audioFormat, codec.readAll().toByteBuffer());
    }

    @Override
    public void setGain(float volume) throws LWJALLException {
        super.setGain(volume);
        if(channel != null)
        {
            AL10.alSourcef(channel.getSource(0), AL10.AL_GAIN, (getGain()));
        }
    }

}
