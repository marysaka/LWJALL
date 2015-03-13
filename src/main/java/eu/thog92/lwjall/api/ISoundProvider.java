package eu.thog92.lwjall.api;

import java.net.URL;

/*
 * A sound provider. Loads and plays sounds
 */
public interface ISoundProvider
{

    /**
     * Sets the listener location
     * 
     * @param x
     *            The position of the listener on X axis
     * @param y
     *            The position of the listener on Y axis
     * @param z
     *            The position of the listener on Z axis
     */
    void setListenerLocation(float x, float y, float z);

    /**
     * Changes the listeners orientation using the specified coordinates.
     *
     * @param lookX
     *            X element of the look-at direction.
     * @param lookY
     *            Y element of the look-at direction.
     * @param lookZ
     *            Z element of the look-at direction.
     * @param upX
     *            X element of the up direction.
     * @param upY
     *            Y element of the up direction.
     * @param upZ
     *            Z element of the up direction.
     */
    void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ);

    /**
     * Disposes all resources used by the provider
     */
    void cleanup();

    /**
     * Plays a given source
     * 
     * @param sourceName
     *            The source to play
     * @throws NullPointerException
     *             Thrown if no source was found with given sourceName
     */
    void play(String sourceName);

    /**
     * Returns if the given source is playing
     * 
     * @param sourceName
     *            The source to check if it is playing
     * @return
     *         <code>true</code> if the source is playing, <code>false</code> if not or if the source doesn't exist
     */
    boolean isPlaying(String sourceName);

    /**
     * Creates a new {@link AbstractSource}
     * 
     * @param sourceName
     *            The source's name
     * @param url
     *            The {@link URL} from which to get the audio stream
     * @param streaming
     *            <code>true</code> if the audio should be streamed, <code>false</code> if the whole sound should be loaded
     * @return
     *         The new {@link AbstractSource}
     */
    AbstractSource newSource(String sourceName, URL url, boolean streaming);

    /**
     * Creates a new {@link AbstractSource}
     * 
     * @param sourceName
     *            The source's name
     * @param url
     *            The {@link URL} from which to get the audio stream
     * @param type
     *            The type identifier of the audio data
     * @param streaming
     *            <code>true</code> if the audio should be streamed, <code>false</code> if the whole sound should be loaded
     * @return
     *         The new {@link AbstractSource}
     */
    AbstractSource newSource(String sourceName, URL url, String type, boolean streaming);

    /**
     * The {@link ICodecManager} loaded by this provider
     * 
     * @return
     *         The {@link ICodecManager} used by this provider
     */
    ICodecManager getCodecManager();

    /**
     * Updates the provider
     */
    void update();

    /**
     * Returns if this provider support pitch
     * 
     * @return
     *         <code>true</code> if pitch is supported, <code>false</code> if not
     */
    boolean supportsPitch();


    /**
     * Sets the gain of the all playing sources. Ranges from 0 to 1
     */
    void setMasterGain(float gain);


    /**
     * Returns the global gain
     *
     * @return
     *         The global gain
     */
    float getMasterGain();
}
