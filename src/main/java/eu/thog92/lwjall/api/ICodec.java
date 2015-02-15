package eu.thog92.lwjall.api;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import eu.thog92.lwjall.internal.sources.StreamingSource;

import java.io.IOException;
import java.net.URL;

public interface ICodec
{
    /**
     * Should make any preparations required before reading from the audio stream.
     * If another stream is already opened, it should be closed and a new audio
     * stream opened in its place. This method is used internally
     * not only to initialize a stream, but also to rewind streams and to switch
     * stream sources on the fly.
     * 
     * @param url
     *            The {@link URL} from which to fetch the audio data
     * @param channel
     *            The {@link IChannel} in which to prepare the audio data
     * @return False if an error occurred or if end of stream was reached.
     * @throws UnsupportedAudioFileException
     *             Thrown if the file given via the <code>url</code> parameter is not supported by this codec
     */
    boolean initialize(URL url, IChannel channel) throws IOException, UnsupportedAudioFileException;

    /**
     * Should return false if the stream is busy initializing.  To prevent bad
     * data from being returned by this method, derived classes should internally
     * synchronize with any elements used by both the initialized() and initialize()
     * methods.
     * @return True if steam is initialized.
     */
    boolean initialized();

    /**
     * Disposes all resources used by this codec, including the stream
     * 
     * @throws IOException
     *             Thrown in case the stream couldn't be closed correctly
     */
    void cleanup() throws IOException;

    /**
     * Reads in the stream
     * 
     * @param n
     *            The meaning of n is implementation-dependent. For instance, for wave files it is a number of bytes whereas for Vorbis files it represents a number of buffers
     * @return
     *         An AudioBuffer with the requested data
     * @throws IOException
     */
    AudioBuffer read(int n) throws IOException;

    /**
     * Reads all data from the stream
     * 
     * @return
     *         An AudioBuffer containing all the sound data
     * @throws IOException
     */
    AudioBuffer readAll() throws IOException;

    /**
     * Returns the audio format of the data being returned by the {@link ICodec#read(int)} and {@link ICodec#readAll()} methods.
     * 
     * @return The {@link AudioFormat} of the data.
     */
    AudioFormat getAudioFormat();

    /**
     * Sets the {@link AudioFormat} of the codec
     * 
     * @param format
     *            The new format
     */
    void setAudioFormat(AudioFormat format);

    /**
     * Updates the codec. Mostly used in {@link StreamingSource}.
     * 
     * @param buffersProcessed
     *            The number of buffers processed since last call
     */
    void update(int buffersProcessed);

    /**
     * Prepares <code>n</code> buffers to be used next while streaming
     * @param n
     *            The number of buffers to prepare
     * @return
     *         True if the end of stream/file has been reached
     * @throws IOException
     *             Exception thrown if any error occured while reading in the stream
     */
    boolean prepareBuffers(int n) throws IOException;

}
