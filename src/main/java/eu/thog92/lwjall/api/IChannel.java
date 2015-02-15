package eu.thog92.lwjall.api;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface IChannel
{

    /**
     * Returns the sample rate of the audio
     * 
     * @return
     *         The sample rate of the audio
     */
    int getSampleRate();

    /**
     * Sets the sampleRate of the audio
     * 
     * @param sampleRate
     *            The new rate
     */
    void setSampleRate(int sampleRate);

    /**
     * Returns the source ID at given index
     * 
     * @param index
     *            The index of the source
     * @return
     *         The source id
     */
    int getSource(int index);

    /**
     * Disposes all resources used by the channel
     */
    void cleanup();

    /**
     * Sets the {@link AudioFormat} of the audio data
     * 
     * @param audioFormat
     *            The {@link AudioFormat} to set
     */
    void setAudioFormat(AudioFormat audioFormat);

    /**
     * Plays or resumes the current sound
     */
    void play();

    /**
     * Pauses the current sound
     */
    void pause();

    /**
     * Stops the current sound
     */
    void stop();

    /**
     * Rewinds the current sound
     */
    void rewind();

    /**
     * Returns if the channel is currently playing sound
     * 
     * @return
     *         <code>true</code> if the channel is playing sound, <code>false</code> if not
     */
    boolean isPlaying();

    /**
     * Returns for how long the channel has been playing
     * 
     * @return
     *         The time since the channel started playing
     */
    float getPlayingDuration();

    /**
     * Setups the whole channel. Mostly used for little sound effects that can be saved in the RAM
     * 
     * @param audioFormat
     *            The {@link AudioFormat} of the sound
     * @param buffer
     *            The raw audio data
     * @throws Exception
     *             Thrown if anything wrong happens while setting up the channel
     */
    void setup(AudioFormat audioFormat, ByteBuffer buffer) throws Exception;

    /**
     * Returns if the channel has stopped playing or not
     * 
     * @return
     *         <code>true</code> if the channel has stopped playing, <code>false</code> if not
     */
    boolean hasStopped();

    /**
     * Returns the gain of the channel
     * 
     * @return
     *         The gain of the channel
     */
    float getGain();

    /**
     * Sets the gain of this channel
     * 
     * @param gain
     *            The new gain to set
     */
    void setGain(float gain);

    /**
     * Sets the velocity of the sound played by this channel
     * 
     * @param velocity
     *            The new velocity, stored in XYZ order
     */
    void setVelocity(FloatBuffer velocity);

    /**
     * Sets the position of the sound played by this channel
     * 
     * @param pos
     *            The new position, stored in XYZ order
     */
    void setPosition(FloatBuffer pos);

    /**
     * Returns the format of this channel
     * 
     * @return
     *         The format of the channel
     */
    int getFormat();
}
