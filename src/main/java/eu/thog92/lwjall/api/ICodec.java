package eu.thog92.lwjall.api;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;
import java.net.URL;

public interface ICodec
{
    /**
     * Should make any preperations required before reading from the audio stream.
     * If another stream is already opened, it should be closed and a new audio
     * stream opened in its place. This method is used internally
     * not only to initialize a stream, but also to rewind streams and to switch
     * stream sources on the fly.
     * 
     * @return False if an error occurred or if end of stream was reached.
     * @throws UnsupportedAudioFileException
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
     * Should return the audio format of the data being returned by the read() and
     * readAll() methods.
     * @return Information wrapped into an AudioFormat context.
     */
    AudioFormat getAudioFormat();

    void setAudioFormat(AudioFormat format);

    void update(int buffersProcessed);

    boolean prepareBuffers(int n) throws IOException;

}
