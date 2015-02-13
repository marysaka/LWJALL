package eu.thog92.lwjall.codecs;

import eu.thog92.lwjall.AudioBuffer;
import eu.thog92.lwjall.IChannel;
import eu.thog92.lwjall.ICodec;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class WaveCodec implements ICodec
{
    private AudioFormat format;
    private boolean     isInitialized;
    private InputStream input;
    private IChannel    channel;
    private int         buffers;
    private boolean     eof;

    @Override
    public boolean initialize(URL url, IChannel channel) throws IOException
    {
        this.isInitialized = true;
        this.input = url.openStream();
        this.channel = channel;
        prepareBuffers(2);
        return false;
    }

    private boolean prepareBuffers(int n) throws IOException
    {
        if(input.available() <= 0)
        {
            return true;
        }
        for(int i = 0; i < n; i++)
        {
            buffers++;
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
    public boolean initialized()
    {
        return false;
    }

    @Override
    public void cleanup()
    {
        this.format = null;
    }

    @Override
    public AudioBuffer read()
    {
        return null;
    }

    @Override
    public AudioFormat getAudioFormat()
    {
        return format;
    }

    @Override
    public void setAudioFormat(AudioFormat format)
    {
        this.format = format;
    }

    @Override
    public void update(int buffersProcessed)
    {

        buffers -= buffersProcessed;
        if(buffers == 0 && eof)
        {
            channel.stop();
        }
        if(buffers < 2)
        {
            try
            {
                eof = prepareBuffers(2);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
