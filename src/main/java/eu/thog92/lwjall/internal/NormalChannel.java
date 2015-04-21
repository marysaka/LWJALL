package eu.thog92.lwjall.internal;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.util.LWJALLException;
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

    public NormalChannel(IntBuffer source) throws LWJALLException {
        this.source = source;
        stopped = true;
        setGain(1f);
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
    public void setAudioFormat(AudioFormat audioFormat) throws LWJALLException {
        int soundFormat;
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
                throw new LWJALLException("Illegal sample size in method 'setAudioFormat' ("+audioFormat.getSampleSizeInBits()+")");
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
                throw new LWJALLException("Illegal sample size in method 'setAudioFormat' ("+audioFormat.getSampleSizeInBits()+")");
            }
        }
        else
        {
            throw new LWJALLException("Audio data neither mono nor stereo in " + "method 'setAudioFormat'");
        }
        format = soundFormat;
        sampleRate = (int) audioFormat.getSampleRate();
    }

    @Override
    public void play() throws LWJALLException {
        stopped = false;
        AL10.alSourcePlay(source.get(0));
        checkALError("playing source");
    }

    @Override
    public void pause() throws LWJALLException {
        AL10.alSourcePause(source.get(0));
        checkALError("pausing source");
    }

    @Override
    public void stop() throws LWJALLException {
        stopped = true;
        AL10.alSourceStop(source.get(0));
        checkALError("stopping source");
    }

    @Override
    public void rewind() throws LWJALLException {
        AL10.alSourceRewind(source.get(0));
        checkALError("rewinding source");
    }

    @Override
    public boolean isPlaying() throws LWJALLException {
        boolean state = AL10.alGetSourcei(source.get(0), AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
        checkALError("checking playing state");
        return state;
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
    public void setup(AudioFormat audioFormat, ByteBuffer buffer) throws LWJALLException {
        setAudioFormat(audioFormat);
        bufferPointer = AL10.alGenBuffers();
        checkALError("creating buffer");

        AL10.alBufferData(bufferPointer, format, buffer, sampleRate);
        checkALError("filling buffer");

        AL10.alSourcei(source.get(0), AL10.AL_BUFFER, bufferPointer);
        checkALError("setting buffer pointer");
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
    public void setGain(float gain) throws LWJALLException {
        this.gain = gain;
        AL10.alSourcef(source.get(0), AL10.AL_GAIN, gain);
        checkALError("changing gain");
    }

    @Override
    public void setVelocity(FloatBuffer velocity) throws LWJALLException {
        AL10.alSource(source.get(0), AL10.AL_VELOCITY, velocity);
        checkALError("setting velocity");
    }

    @Override
    public void setPosition(FloatBuffer pos) throws LWJALLException {
        AL10.alSource(source.get(0), AL10.AL_POSITION, pos);
        checkALError("setting position");
    }

    @Override
    public int getFormat()
    {
        return format;
    }

    private void checkALError(String reason) throws LWJALLException {
        String error = null;
        switch(AL10.alGetError()) {
            case AL10.AL_NO_ERROR:
                break;
            case AL10.AL_INVALID_NAME:
                error = "Invalid name parameter";
                break;
            case AL10.AL_INVALID_ENUM:
                error = "Invalid parameter";
                break;
            case AL10.AL_INVALID_VALUE:
                error = "Invalid enumerated parameter value";
                break;
            case AL10.AL_INVALID_OPERATION:
                error = "Illegal call";
                break;
            case AL10.AL_OUT_OF_MEMORY:
                error = "Unable to allocate memory";
                break;
            default:
                error = "An unknown error occurred";
                break;
        }

        if(error != null) {
            throw new LWJALLException("OpenAL error: "+error+" when "+reason);
        }
    }

    public boolean addToBufferQueue(byte[] buffer) throws LWJALLException {
        ByteBuffer byteBuffer = (ByteBuffer) BufferUtils.createByteBuffer(
                buffer.length).put(buffer).flip();
        IntBuffer intBuffer = BufferUtils.createIntBuffer(1);

        AL10.alSourceUnqueueBuffers(source.get(0), intBuffer);
        checkALError("unqueuing buffer");

        AL10.alBufferData(intBuffer.get(0), format, byteBuffer, sampleRate);
        checkALError("filling buffer");

        AL10.alSourceQueueBuffers(source.get(0), intBuffer);
        checkALError("queuing buffer");

        return true;
    }

}
