package eu.thog92.lwjall.api;

import eu.thog92.lwjall.internal.sources.StreamingSource;
import eu.thog92.lwjall.util.Buffers;

import java.net.URL;
import java.nio.FloatBuffer;

/**
 * A sound source. Contains position and velocity informations
 */
public abstract class AbstractSource
{
    /**
     * The {@link ICodecManager} used by this source
     */
    protected final ICodecManager  codecManager;

    /**
     * The {@link ISoundProvider} used by this source
     */
    protected final ISoundProvider soundProvider;

    /**
     * The {@link ICodec} used by this source
     */
    protected ICodec codec;

    /**
     * The {@link IChannel} used by this source
     */
    protected IChannel channel;

    /**
     * The position of the source
     */
    protected FloatBuffer position;

    /**
     * The velocity of the sound played by this source
     */
    protected FloatBuffer velocity;

    /**
     * The name of this source
     */
    private String name;

    /**
     * The pitch of this source
     */
    private float pitch;

    /**
     * The distance from the listener
     */
    private float distanceFromListener;

    /**
     * The gain of this source
     */
    private float gain = 1.0F;

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

    /**
     * Creates a new source
     *
     * @param soundProvider
     *            The {@link ISoundProvider} to use for this source
     * @param sourceName
     *            The source name
     * @param channel
     *            The {@link IChannel} to use for this source
     */
    public AbstractSource(ISoundProvider soundProvider, String sourceName, IChannel channel)
    {
        this.soundProvider = soundProvider;
        this.codecManager = soundProvider.getCodecManager();
        position = Buffers.createFloatBuffer(3);
        velocity = Buffers.createFloatBuffer(3);
        this.name = sourceName;
        this.channel = channel;
    }

    /**
     * Returns the activity state if this source
     * 
     * @return
     *         <code>true</code> if this source is active, and <code>false</code> if not
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Returns the pausing state if this source
     * 
     * @return
     *         <code>true</code> if this source is paused, and <code>false</code> if not
     */
    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Returns the stop state if this source
     * 
     * @return
     *         <code>true</code> if this source is stopped, and <code>false</code> if not
     */
    public boolean isStopped()
    {
        return stopped;
    }

    /**
     * Returns the pitch from this source
     * 
     * @return
     *         The pitch of this source
     */
    public float getPitch()
    {
        return pitch;
    }

    /**
     * Sets the pitch of the source. Ranges from 0 to 1
     * 
     * @param value
     *            The new pitch to apply.
     */
    public void setPitch(float value)
    {
        checkRange(value, 0, 1);
        this.pitch = value;
    }

    /**
     * Checks if the value is in given range. If not, an exception is thrown.
     * 
     * @param value
     *            The value to check
     * @param min
     *            The minimal acceptable value, included
     * @param max
     *            The maximal acceptable value, included
     */
    private void checkRange(float value, float min, float max)
    {
        if(value > max || min > value) throw new IllegalArgumentException("Value " + value + " is not in range [" + min + ", " + max + "]");
    }

    /**
     * Returns the gain from this source
     * 
     * @return
     *         The gain of this source
     */
    public float getGain()
    {
        return gain;
    }

    /**
     * Sets the gain of the source. Ranges from 0 to 1
     */
    public void setGain(float gain)
    {
        checkRange(gain, 0, 1);
        this.gain = gain * soundProvider.getMasterGain();
        if(channel != null) channel.setGain(gain);
    }

    /**
     * Sets the position of the sound played by this source
     * 
     * @param x
     *            Position on X axis
     * @param y
     *            Position on Y axis
     * @param z
     *            Position on Z axis
     */
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

    /**
     * Sets the velocity of the sound played by this source
     * 
     * @param x
     *            Velocity on X axis
     * @param y
     *            Velocity on Y axis
     * @param z
     *            Velocity on Z axis
     */
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

    /**
     * <b>(WIP)</b>
     * Returns the distance from the listener
     * 
     * @return
     *         A distance in meters from the listener
     */
    public float getDistanceFromListener()
    {
        return distanceFromListener;
    }

    /**
     * Returns the name of this source
     * 
     * @return
     *         The source name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Prepares the source before playing it
     * 
     * @param url
     *            The {@link URL} containing the audio data
     * @param type
     *            The identifier of the audio data (eg. <code>"ogg"</code>)
     * @throws Exception
     */
    public abstract void setup(URL url, String type) throws Exception;

    /**
     * Updates this sources. (Cycle in buffers in {@link StreamingSource} for instance
     */
    public void update()
    {
        ;
    }

    /**
     * Returns the bound {@link IChannel}
     * 
     * @return
     *         The {@link IChannel} used by this source
     */
    public IChannel getChannel()
    {
        return channel;
    }
}
