package eu.thog92.lwjall;

import java.nio.IntBuffer;

public class Channel
{
    /**
     * OpenAL's IntBuffer identifier for this channel.
     */
    private IntBuffer source;

    /**
     * The sample rate for this channel
     */
    private int sampleRate;


    public Channel(IntBuffer source)
    {
        this.source = source;
    }

    public int getSampleRate()
    {
        return  sampleRate;
    }

    public int getFromALSource(int index)
    {
        return source.get(index);
    }
}
