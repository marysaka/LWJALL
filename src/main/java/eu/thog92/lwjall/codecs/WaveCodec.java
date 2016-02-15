package eu.thog92.lwjall.codecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import eu.thog92.lwjall.api.AudioBuffer;
import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodec;

import eu.thog92.lwjall.util.LWJALLException;
import org.lwjgl.openal.AL10;

public class WaveCodec implements ICodec
{

    /**
     * The format of the audio data
     */
    private AudioFormat format;

    /**
     * Is the codec initialized?
     */
    private boolean     isInitialized;

    /**
     * The input stream
     */
    private InputStream input;

    /**
     * The channel used by the codec
     */
    private IChannel    channel;

    /**
     * How many buffers loaded for streaming ?
     */
    private int         buffers;

    /**
     * Did we reach End Of File ?
     */
    private boolean     eof;

    @Override
    public boolean initialize(URL url, IChannel channel) throws LWJALLException
    {
        this.isInitialized = true;
        try {
            this.input = url.openStream();
            this.channel = channel;
            format = AudioSystem.getAudioFileFormat(url).getFormat();

            channel.setAudioFormat(format);
        } catch (Exception e) {
            throw new LWJALLException("Failed to init wave codec", e);
        }
        return true;
    }

    @Override
    public boolean prepareBuffers(int n) throws LWJALLException
    {
        try {
            if(input.available() <= 0)
            {
                return true;
            }
        } catch (IOException e) {
            throw new LWJALLException("Failed to prepare buffers", e);
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
    public void cleanup() throws LWJALLException
    {
        this.format = null;
        buffers = 0;
        try {
            input.close();
        } catch (IOException e) {
            throw new LWJALLException("Failed to close stream", e);
        }
    }

    @Override
    public AudioBuffer read(int n) throws LWJALLException
    {
        if(n < 0) throw new NegativeArraySizeException("Negative reading size asked: " + n);
        byte[] data = new byte[n];
        int length = 0;
        try {
            length = input.read(data);
        } catch (IOException e) {
            throw new LWJALLException("Failed to read the sound file", e);
        }
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
    public void update(int buffersProcessed) throws LWJALLException {
        for(int i = 0; i < buffersProcessed && !eof; i++ )
        {
            AL10.alSourceUnqueueBuffers(channel.getSource(0));
            eof = prepareBuffers(2);
            channel.play();
        }
        if(eof)
        {
            channel.stop();
        }
    }

    @Override
    public AudioBuffer readAll() throws LWJALLException
    {
        byte[] data = new byte[4096];
        int length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while((length = input.read(data)) != -1)
            {
                out.write(data, 0, length);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new LWJALLException("Failed to read sound file", e);
        }
        return new AudioBuffer(out.toByteArray(), getAudioFormat());
    }
}
