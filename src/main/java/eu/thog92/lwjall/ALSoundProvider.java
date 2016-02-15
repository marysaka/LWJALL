package eu.thog92.lwjall;

import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodecManager;
import eu.thog92.lwjall.api.ISoundProvider;
import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.codecs.VorbisCodec;
import eu.thog92.lwjall.codecs.WaveCodec;
import eu.thog92.lwjall.internal.ALCodecManager;
import eu.thog92.lwjall.internal.NormalChannel;
import eu.thog92.lwjall.internal.sources.DirectSource;
import eu.thog92.lwjall.internal.sources.StreamingSource;

import eu.thog92.lwjall.util.LWJALLException;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, AbstractSource> sources;

    private ALCodecManager codecManager;

    private float masterGain = 1.0F;

    public ALSoundProvider() throws LWJALLException {
        ALDevice device = ALDevice.create();
        ALContext context = ALContext.create(device);
        context.makeCurrentThread();
        codecManager = new ALCodecManager();
        codecManager.registerCodec("wav", WaveCodec.class);
        codecManager.registerCodec("ogg", VorbisCodec.class);

        System.out.println("Initializing LWJALL...");
        String errorMessage = checkALError();
        if(errorMessage != null)
        {
            throw new LWJALLException("OpenAL did not initialize properly: " + errorMessage);
        }

        System.out.println("OpenAL initialized.");

        listenerOrientation = BufferUtils.createFloatBuffer(6).put(new float[]
                {
                        0, 0, 0, 0, 0, 0
                });

        // FLIP
        listenerOrientation.flip();

        // Pass the buffers to the sound system, and check for potential errors:
        setListenerLocation(0,0,0);
        errorMessage = checkALError();
        setListenerOrientation(0,0,1,0,1,0);
        errorMessage = checkALError();
        setListenerVelocity(0,0,0);
        errorMessage = checkALError();

        AL10.alDopplerFactor(0.0F);
        errorMessage = checkALError();

        AL10.alDopplerVelocity(1.0F);
        errorMessage = checkALError();

        if(errorMessage != null)
        {
            throw new LWJALLException("OpenAL did not initialize properly: " + errorMessage);
        }

        channels = new ArrayList<IChannel>();

        // Init Channels

        // TODO: Find a real number of Channels
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
        sources = new HashMap<String, AbstractSource>();

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

    private NormalChannel createNormalChannel() throws LWJALLException
    {
        int source = AL10.alGenSources();

        if(AL10.alGetError() != AL10.AL_NO_ERROR)
        {
            return null;
        }

        return new NormalChannel(source);
    }

    /**
     * Update OpenAL listener position
     * @param x
     *            The position of the listener on X axis
     * @param y
     *            The position of the listener on Y axis
     * @param z
     *            The position of the listener on Z axis
     */
    @Override
    public void setListenerLocation(float x, float y, float z)
    {
        AL10.alListener3f(AL10.AL_POSITION, x, y, z);
    }

    /**
     * Sets the listener orientation
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
    @Override
    public void setListenerOrientation(float lookX, float lookY, float lookZ, float upX, float upY, float upZ)
    {
        listenerOrientation.put(0, lookX);
        listenerOrientation.put(1, lookY);
        listenerOrientation.put(2, lookZ);
        listenerOrientation.put(3, upX);
        listenerOrientation.put(4, upY);
        listenerOrientation.put(5, upZ);
        AL10.alListenerfv(AL10.AL_ORIENTATION, listenerOrientation);
    }

    @Override
    public void setListenerVelocity(float x, float y, float z)
    {
        AL10.alListenerfv(AL10.AL_VELOCITY, listenerOrientation);
    }

    @Override
    public void cleanup()
    {
        System.out.println("LWJALL shutting down...");
        ALC.destroy();
        System.out.println("OpenAL destroyed");
    }

    @Override
    public void play(String sourceName) throws LWJALLException
    {
        AbstractSource source = sources.get(sourceName);
        if(source == null)
        {
            throw new NullPointerException("The source " + sourceName + " does not exist");
        }
        source.getChannel().play();
    }

    @Override
    public AbstractSource newSource(String sourceName, URL url, boolean streaming) throws LWJALLException
    {
        String type = url.getFile().substring(url.getFile().lastIndexOf(".") + 1);
        return newSource(sourceName, url, type, streaming);
    }

    @Override
    public AbstractSource newSource(String sourceName, URL url, String type, boolean streaming) throws LWJALLException
    {
        AbstractSource source = null;
        if(streaming)
        {
            source = new StreamingSource(this, sourceName, freeChannel());
        }
        else
        {
            source = new DirectSource(this, sourceName, freeChannel());
        }

        try
        {
            source.setup(url, type);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        sources.put(sourceName, source);
        return source;
    }

    @Override
    public ICodecManager getCodecManager()
    {
        return codecManager;
    }

    private IChannel freeChannel() throws LWJALLException
    {
        for(IChannel channel : channels)
        {
            if(channel.hasStopped())
            {
                return channel;
            }
        }
        // Panic, we don't have any more channels, so create a new one
        IChannel newChannel = createNormalChannel();
        channels.add(newChannel);
        return newChannel;
    }

    @Override
    public boolean isPlaying(String sourceName) throws LWJALLException
    {
        AbstractSource source = sources.get(sourceName);
        return source != null && source.isPlaying();
    }

    @Override
    public void update() throws LWJALLException
    {
        for(AbstractSource s : sources.values())
        {
            s.update();
        }
    }

    @Override
    public boolean supportsPitch()
    {
        return supportPitch;
    }

    @Override
    public void setMasterGain(float gain) throws LWJALLException
    {
        this.masterGain = gain;

        for(AbstractSource s : sources.values())
        {
            s.setGain(s.getGain());
        }
    }

    @Override
    public float getMasterGain()
    {
        return masterGain;
    }
}
