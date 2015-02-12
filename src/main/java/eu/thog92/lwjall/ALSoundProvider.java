package eu.thog92.lwjall;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ALSoundProvider implements ISoundProvider
{
    /**
     * Position of the listener in 3D space.
     */
    private FloatBuffer listenerPosition = null;

    /**
     * Information about the listener's orientation.
     */
    private FloatBuffer listenerOrientation = null;

    /**
     * Velocity of the listener.
     */
    private FloatBuffer listenerVelocity = null;

    private List<IChannel> channels;

    private boolean supportPitch;

    public ALSoundProvider() throws LWJGLException
    {
        System.out.println("Initializing LWJALL...");
        AL.create();
        String errorMessage = checkALError();
        if(errorMessage != null)
        {
            throw new LWJGLException("OpenAL did not initialize properly: " + errorMessage);
        }

        System.out.println("OpenAL initialized.");

        listenerPosition = BufferUtils.createFloatBuffer(3).put(new float[] {0, 0, 0});
        listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[] {0, 0, 0, 0, 0, 0});
        listenerVelocity = BufferUtils.createFloatBuffer(3).put(new float[] {0, 0, 0});

        // FLIP
        listenerPosition.flip();
        listenerOrientation.flip();
        listenerVelocity.flip();

        // Pass the buffers to the sound system, and check for potential errors:
        AL10.alListener(AL10.AL_POSITION, listenerPosition);
        errorMessage = checkALError();
        AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation);
        errorMessage = checkALError();
        AL10.alListener(AL10.AL_VELOCITY, listenerVelocity);
        errorMessage = checkALError();

        AL10.alDopplerFactor(0.0F);
        errorMessage = checkALError();

        AL10.alDopplerVelocity(1.0F);
        errorMessage = checkALError();

        if(errorMessage != null)
        {
            throw new LWJGLException("OpenAL did not initialize properly: " + errorMessage);
        }

        channels = new ArrayList<IChannel>();

        // Init Channels

        //TODO: Find a real number of Channels
        for(int i = 0; i < 28; i++)
        {
            NormalChannel channel = createNormalChannel();
            if(channel == null)
            {
                break;
            }
            channels.add(channel);
        }

        // Pitch Check

        NormalChannel pitchTesting = (NormalChannel) channels.get(1);
        AL10.alSourcef(pitchTesting.getSource(0), AL10.AL_PITCH, 1.0f);
        if(checkALError() != null)
        {
            this.supportPitch = false;
            System.err.println("Pitch Support Disabled! (Unsupported)");
        }
        else
        {
            this.supportPitch = true;
        }
        System.out.println("LWJALL is ready!");
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

    private NormalChannel createNormalChannel()
    {
        IntBuffer source;

        source = BufferUtils.createIntBuffer(1);
        AL10.alGenSources(source);

        if(AL10.alGetError() != AL10.AL_NO_ERROR)
        {
            return null;
        }

        return new NormalChannel(source);
    }

    @Override
    public void setListenerLocation(float x, float y, float z)
    {
        listenerPosition.put(0, x);
        listenerPosition.put(1, y);
        listenerPosition.put(2, z);

        // Update OpenAL listener position:
        AL10.alListener(AL10.AL_POSITION, listenerPosition);
    }

    @Override
    public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
    {
        listenerOrientation.put(0, lookX);
        listenerOrientation.put(1, lookY);
        listenerOrientation.put(2, lookZ);
        listenerOrientation.put(3, upX);
        listenerOrientation.put(4, upY);
        listenerOrientation.put(5, upZ);
        AL10.alListener(AL10.AL_ORIENTATION, listenerOrientation);
    }

    @Override
    public void cleanUp()
    {
        System.out.println("LWALL shutting down...");
        AL.destroy();
        System.out.println("OpenAL destroyed");

    }
}
