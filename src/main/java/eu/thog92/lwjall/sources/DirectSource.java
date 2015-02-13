package eu.thog92.lwjall.sources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.ICodec;
import eu.thog92.lwjall.ICodecManager;
import eu.thog92.lwjall.Source;
import eu.thog92.lwjall.util.Buffers;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL10;

public class DirectSource extends Source
{

    public DirectSource(ICodecManager codecManager)
    {
        super(codecManager);
    }

    @Override
    public void setup(URL url, String type) throws IOException, LWJGLException
    {
        System.out.println(url);
        ICodec codec = codecManager.getCodec(type);
        try
        {
            codec.initialize(url, channel);
        }
        catch(UnsupportedAudioFileException e)
        {
            throw new IOException("Invalid file", e);
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
