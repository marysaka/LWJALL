package eu.thog92.lwjall.internal;

import eu.thog92.lwjall.api.IChannel;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 */
public class NormalChannel implements IChannel
{
    /**
     * OpenAL's IntBuffer identifier for this channel.
     */
    private IntBuffer source;

    /**
     * The sample rate for this channel
     */
    private int sampleRate;

    /**
     * OpenAL data format to use when playing back the assigned source.
     */
    private int format;

    private int bufferPointer;

    private boolean stopped;

    private float gain;

    public NormalChannel(IntBuffer source)
    {
        this.source = source;
        stopped = true;
        gain = 1.f;
    }

    @Override
    public int getSampleRate()
    {
        return sampleRate;
    }

    @Override
    public void setSampleRate(int sampleRate)
    {
        this.sampleRate = sampleRate;
    }

    @Override
    public int getSource(int index)
    {
        return source.get(index);
    }

    @Override
    public void cleanup()
    {
        if(source != null)
        {
            // Stop the Sound
            AL10.alSourceStop(source);

            // Delete OpenAL Source
            AL10.alDeleteSources(source);
            source.clear();
            source = null;
        }
        else
        {
            System.err.println("Attending to cleanup a Source with a empty buffer");
        }
    }

    @Override
    public void setAudioFormat(AudioFormat audioFormat)
    {
        int soundFormat = 0;
        if(audioFormat.getChannels() == 1)
        {
            if(audioFormat.getSampleSizeInBits() == 8)
            {
                soundFormat = AL10.AL_FORMAT_MONO8;
            }
            else if(audioFormat.getSampleSizeInBits() == 16)
            {
                soundFormat = AL10.AL_FORMAT_MONO16;
            }
            else
            {
                System.err.println("Illegal sample size in method " + "'setAudioFormat'");
                return;
            }
        }
        else if(audioFormat.getChannels() == 2)
        {
            if(audioFormat.getSampleSizeInBits() == 8)
            {
                soundFormat = AL10.AL_FORMAT_STEREO8;
            }
            else if(audioFormat.getSampleSizeInBits() == 16)
            {
                soundFormat = AL10.AL_FORMAT_STEREO16;
            }
            else
            {
                System.err.println("Illegal sample size in method " + "'setAudioFormat'");
                return;
            }
        }
        else
        {
            System.err.println("Audio data neither mono nor stereo in " + "method 'setAudioFormat'");
            return;
        }
        format = soundFormat;
        sampleRate = (int) audioFormat.getSampleRate();
    }

    @Override
    public void play()
    {
        stopped = false;
        AL10.alSourcef(source.get(0), AL10.AL_GAIN, 1);
        AL10.alSourcePlay(source.get(0));
    }

    @Override
    public void pause()
    {
        AL10.alSourcePause(source.get(0));
    }

    @Override
    public void stop()
    {
        stopped = true;
        AL10.alSourceStop(source.get(0));
    }

    @Override
    public void rewind()
    {
        AL10.alSourceRewind(source.get(0));
    }

    @Override
    public boolean isPlaying()
    {
        return AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    @Override
    public float getPlayingDuration()
    {
        float bytesPerFrame = 1f;
        switch(format)
        {
            case AL10.AL_FORMAT_MONO8:
                bytesPerFrame = 1f;
                break;
            case AL10.AL_FORMAT_MONO16:
                bytesPerFrame = 2f;
                break;
            case AL10.AL_FORMAT_STEREO8:
                bytesPerFrame = 2f;
                break;
            case AL10.AL_FORMAT_STEREO16:
                bytesPerFrame = 4f;
                break;
            default:
                System.out.println("Unknown format: " + format);
                break;
        }
        return (((float) AL10.alGetSourcei(source.get(0), AL11.AL_BYTE_OFFSET) / bytesPerFrame) / (float) sampleRate) * 1000;
    }

    @Override
    public void setup(AudioFormat audioFormat, ByteBuffer buffer) throws LWJGLException
    {
        setAudioFormat(audioFormat);
        bufferPointer = AL10.alGenBuffers();
        int error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR)
        {
            throw new LWJGLException("Error while creating sound buffer: " + error);
        }
        AL10.alBufferData(bufferPointer, format, buffer, sampleRate);

        AL10.alSourcei(source.get(0), AL10.AL_BUFFER, bufferPointer);

        error = AL10.alGetError();
        if(error != AL10.AL_NO_ERROR)
        {
            throw new LWJGLException("Error while uploading sound buffer: " + error);
        }
    }

    @Override
    public boolean hasStopped()
    {
        return stopped;
    }

    @Override
    public float getGain()
    {
        return gain;
    }

    @Override
    public void setGain(float gain)
    {
        this.gain = gain;
        AL10.alSourcef(source.get(0), AL10.AL_GAIN, gain);
    }

    @Override
    public void setVelocity(FloatBuffer velocity)
    {
        AL10.alSource(source.get(0), AL10.AL_VELOCITY, velocity);
    }

    @Override
    public void setPosition(FloatBuffer pos)
    {
        AL10.alSource(source.get(0), AL10.AL_POSITION, pos);
    }

    @Override
    public int getFormat()
    {
        return format;
    }

    private String checkALError()
    {
        switch(AL10.alGetError())
        {
            case AL10.AL_NO_ERROR:
                return null;
            case AL10.AL_INVALID_NAME:
                return "Invalid name parameter.";
            case AL10.AL_INVALID_ENUM:
                return "Invalid parameter.";
            case AL10.AL_INVALID_VALUE:
                return "Invalid enumerated parameter value.";
            case AL10.AL_INVALID_OPERATION:
                return "Illegal call.";
            case AL10.AL_OUT_OF_MEMORY:
                return "Unable to allocate memory.";
            default:
                return "An unknow error occurred.";
        }
    }

    public boolean addToBufferQueue(byte[] buffer)
    {
        ByteBuffer byteBuffer = (ByteBuffer) BufferUtils.createByteBuffer(
                buffer.length).put(buffer).flip();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);

        AL10.alSourceUnqueueBuffers(source.get(0), intBuffer);
        if(checkALError() != null)
        {
            return false;
        }

        checkALError();

        AL10.alBufferData(intBuffer.get(0), format, byteBuffer, sampleRate);
        if(checkALError() != null)
        {
            return false;
        }

        AL10.alSourceQueueBuffers(source.get(0), intBuffer);
        if(checkALError() != null)
        {
            return false;
        }

        return true;
    }

}
