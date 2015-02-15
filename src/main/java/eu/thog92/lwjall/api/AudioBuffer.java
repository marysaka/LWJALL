package eu.thog92.lwjall.api;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import eu.thog92.lwjall.util.Buffers;

/**
 * A buffer containing the audio data and the {@link AudioFormat} in which it is stored.
 */
public class AudioBuffer
{
    /**
     * The audio data
     */
    byte[] data;

    /**
     * The format
     */
    AudioFormat format;

    /**
     * Creates a new audio buffer
     * 
     * @param data
     *            The audio data
     * @param format
     *            The {@link AudioFormat} in which the data is stored
     */
    public AudioBuffer(byte[] data, AudioFormat format)
    {
        this.data = data;
        this.format = format;
    }

    /**
     * Disposes the resources used by the buffer
     */
    public void cleanup()
    {
        this.data = null;
        this.format = null;
    }

    /**
     * Trims down the size of the audio data if it is larger than the specified
     * maximum length.
     *
     * @param maxLength
     *            Maximum size this buffer may be.
     */
    public void trimData(int maxLength)
    {
        if(data == null || maxLength == 0)
        {
            data = null;
        }
        else if(data.length > maxLength)
        {
            byte[] trimmedArray = new byte[maxLength];
            System.arraycopy(data, 0, trimmedArray, 0, maxLength);
            data = trimmedArray;
        }
    }

    /**
     * Creates a new flipped {@link ByteBuffer} containing the raw audio data
     * 
     * @see {@link ByteBuffer#flip()}
     * @return
     *         A {@link ByteBuffer} containing the audio data
     */
    public ByteBuffer toByteBuffer()
    {
        return Buffers.flippedByteBuffer(data);
    }
}
