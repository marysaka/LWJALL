package eu.thog92.lwjall.api;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import eu.thog92.lwjall.util.Buffers;

public class AudioBuffer
{
    byte[] data;

    AudioFormat format;

    public AudioBuffer(byte[] data, AudioFormat format)
    {
        this.data = data;
        this.format = format;
    }

    public void cleanUp()
    {
        this.data = null;
        this.format = null;
    }

    /**
     * Trims down the size of the audio data if it is larger than the specified
     * maximum length.
     *
     * @param maxLength Maximum size this buffer may be.
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

    public ByteBuffer toByteBuffer()
    {
        return Buffers.flippedByteBuffer(data);
    }
}
