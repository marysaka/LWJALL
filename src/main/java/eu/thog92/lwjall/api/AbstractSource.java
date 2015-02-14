package eu.thog92.lwjall.api;

import eu.thog92.lwjall.util.Buffers;

import java.net.URL;
import java.nio.FloatBuffer;

public abstract class AbstractSource
{
    protected final ICodecManager codecManager;
    public          ICodec        codec;
    protected       IChannel      channel;
    protected       AudioBuffer   audioBuffer;
    // TODO: Vector?
    protected       FloatBuffer   position;
    protected       FloatBuffer   velocity;
    String name;
    private float pitch;
    private float distanceFromListener;
    private float   gain    = 1.0F;
    private float   volume  = 1.0F;
    /**
     * False when this source gets culled.
     */
    private boolean active  = true;
    /**
     * Whether or not this source has been stopped.
     */
    private boolean stopped = true;
    /**
     * Whether or not this source has been paused.
     */
    private boolean paused  = false;

    public AbstractSource(ICodecManager codecManager, String sourceName, IChannel channel)
    {
        this.codecManager = codecManager;
        position = Buffers.createFloatBuffer(3);
        velocity = Buffers.createFloatBuffer(3);
        this.name = sourceName;
        this.channel = channel;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float value)
    {
        // TODO: Check value validity
        this.pitch = value;
    }

    public float getGain()
    {
        return gain;
    }

    public void setGain(float gain)
    {
        this.gain = gain;
    }

    public float getVolume()
    {
        return volume;
    }

    public void setVolume(float volume)
    {
        this.volume = volume;
    }

    public void setPosition(float x, float y, float z)
    {
        this.position.clear();
        this.position.put(new float[]
                {
                        x, y, z
                });

        position.flip();

        if(channel != null)
        {
            channel.setPosition(position);
        }
    }

    public void setVelocity(float x, float y, float z)
    {
        this.velocity.clear();
        this.velocity.put(new float[]
                {
                        x, y, z
                });

        velocity.flip();

        if(channel != null)
        {
            channel.setVelocity(velocity);
        }
    }

    public float getDistanceFromListener()
    {
        return distanceFromListener;
    }

    public String getName()
    {
        return name;
    }

    public abstract void setup(URL url, String type) throws Exception;

    public void update()
    {
        ;
    }

    public IChannel getChannel()
    {
        return channel;
    }
}
