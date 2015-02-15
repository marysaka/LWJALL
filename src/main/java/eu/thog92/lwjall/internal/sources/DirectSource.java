package eu.thog92.lwjall.internal.sources;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodec;
import eu.thog92.lwjall.api.ICodecManager;
import eu.thog92.lwjall.api.AbstractSource;

import org.lwjgl.openal.AL10;

public class DirectSource extends AbstractSource
{

    public DirectSource(ICodecManager codecManager, String sourceName, IChannel channel)
    {
        super(codecManager, sourceName, channel);
    }

    @Override
    public void setup(URL url, String type) throws Exception
    {
        System.out.println(url);
        this.codec = codecManager.getCodec(type);
        try
        {
            codec.initialize(url, channel);
        }
        catch(UnsupportedAudioFileException e)
        {
            throw new IOException("Invalid file: " + url.toExternalForm(), e);
        }
        AudioFormat audioFormat = codec.getAudioFormat();
        channel.setup(audioFormat, codec.readAll().toByteBuffer());
    }

    @Override
    public void setVolume(float volume)
    {
        super.setVolume(volume);
        if(channel != null)
        {
            AL10.alSourcef(channel.getSource(0), AL10.AL_GAIN, (getGain() * getVolume()));
        }
    }

}
