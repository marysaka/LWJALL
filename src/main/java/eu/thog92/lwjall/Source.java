package eu.thog92.lwjall;

import java.nio.IntBuffer;

public abstract class Source
{
    private String name;

    private float pitch;

    private float distanceFromListener;

    private float gain = 1.0F;

    private float volume = 1.0F;

    protected IChannel channel;

    protected AudioBuffer audioBuffer;

    /**
     * False when this source gets culled.
     */
    private boolean active = true;

    /**
     * Whether or not this source has been stopped.
     */
    private boolean stopped = true;

    /**
     * Whether or not this source has been paused.
     */
    private boolean paused = false;


    //TODO: Vector?
    protected IntBuffer position;

    protected IntBuffer velocity;


    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float value)
    {
        //TODO: Check value validity
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

    public void setPosition(int x, int y, int z)
    {
        this.position.clear();
        this.position.put(new int[] {x, y, z});
    }

    public void setVelocity(int x, int y, int z)
    {
        this.velocity.clear();
        this.velocity.put(new int[] {x, y, z});
    }

    public float getDistanceFromListener()
    {
        return distanceFromListener;
    }
}
