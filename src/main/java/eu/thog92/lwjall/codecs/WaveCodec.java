package eu.thog92.lwjall.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.api.AudioBuffer;
import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodec;

import org.lwjgl.openal.AL10;

public class WaveCodec implements ICodec
{
    private AudioFormat format;
    private boolean     isInitialized;
    private InputStream input;
    private IChannel    channel;
    private int         buffers;
    private boolean     eof;

    @Override
    public boolean initialize(URL url, IChannel channel) throws IOException, UnsupportedAudioFileException
    {
        this.isInitialized = true;
        this.input = url.openStream();
        this.channel = channel;
        format = AudioSystem.getAudioFileFormat(url).getFormat();
        return true;
    }

    public boolean prepareBuffers(int n) throws IOException
    {
        if(input.available() <= 0)
        {
            return true;
        }
        for(int i = 0; i < n; i++)
        {
            buffers++;
            AudioBuffer buffer = read(48000);
            if(buffer == null) return true;
            ByteBuffer audioBuffer = buffer.toByteBuffer();

            int bufferPointer = AL10.alGenBuffers();
            AL10.alBufferData(bufferPointer, channel.getFormat(), audioBuffer, channel.getSampleRate());

            AL10.alSourceQueueBuffers(channel.getSource(0), bufferPointer);
        }
        return false;
    }

    @Override
    public boolean initialized()
    {
        return isInitialized;
    }

    @Override
    public void cleanup() throws IOException
    {
        this.format = null;
        buffers = 0;
        input.close();
    }

    @Override
    public AudioBuffer read(int n) throws IOException
    {
        if(n < 0) throw new NegativeArraySizeException("Negative reading size asked: " + n);
        byte[] data = new byte[n];
        int length = input.read(data);
        if(length == -1)
        {
            return null;
        }
        AudioBuffer buffer = new AudioBuffer(data, getAudioFormat());
        buffer.trimData(length);
        return buffer;
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

    @Override
    public AudioBuffer readAll() throws IOException
    {
        byte[] data = new byte[4096];
        int length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((length = input.read(data)) != -1)
        {
            out.write(data, 0, length);
        }
        out.flush();
        out.close();
        return new AudioBuffer(out.toByteArray(), getAudioFormat());
    }
}
