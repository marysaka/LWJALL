package eu.thog92.lwjall.codecs;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

import org.lwjgl.openal.AL10;

import eu.thog92.lwjall.util.AudioBuffer;
import eu.thog92.lwjall.api.IChannel;
import eu.thog92.lwjall.api.ICodec;
import eu.thog92.lwjall.util.Buffers;

public class VorbisCodec implements ICodec
{

    private boolean       initialized;
    private boolean       eof;
    private IChannel      channel;
    private InputStream   input;
    private URLConnection urlConnection;
    private URL           url;

    // Jorbis stuff
    private int           bufferSize;
    private int           currentIndex;
    private int           bufferLength;
    private byte[]        buffer;
    private StreamState   streamState;
    private Block         block;
    private DspState      dspState;
    private SyncState     syncState;
    private Comment       comment;
    private Info          info;
    private Page          page;
    private Packet        packet;
    private int           convertedSize;
    private AudioFormat   audioFormat;
    private float[][][]   pcmData;
    private int[]         pcmIndex;
    private byte[]        convertedBuffer;
    private int           buffers;

    @Override
    public boolean initialize(URL url, IChannel channel) throws IOException, UnsupportedAudioFileException
    {
        this.channel = channel;
        initialized = true;

        this.url = url;
        this.urlConnection = url.openConnection();
        this.input = urlConnection.getInputStream();

        bufferSize = 4096 * 2;
        currentIndex = 0;
        bufferLength = 0;
        buffer = null;

        streamState = new StreamState();
        dspState = new DspState();
        block = new Block(dspState);
        syncState = new SyncState();
        comment = new Comment();
        info = new Info();
        page = new Page();
        packet = new Packet();

        syncState.init();
        syncState.buffer(bufferSize);
        buffer = syncState.data;

        if(!fetchHeader())
        {
            throw new UnsupportedAudioFileException("Header could not be read");
        }

        convertedSize = bufferSize * 2;
        dspState.synthesis_init(info);
        block.init(dspState);

        int channels = info.channels;
        int freq = info.rate;

        audioFormat = new AudioFormat(freq, 16, channels, true, false);

        pcmData = new float[1][][];
        pcmIndex = new int[info.channels];

        initialized = true;

        return true;
    }

    private boolean fetchHeader() throws IOException
    {
        currentIndex = syncState.buffer(bufferSize);
        int bytes = input.read(syncState.data, currentIndex, bufferSize);
        if(bytes < 0) bytes = 0;

        syncState.wrote(bytes);
        if(syncState.pageout(page) != 1)
        {
            if(bytes < bufferSize) return true; // We read all the file
            return false; // Invalid header
        }

        streamState.init(page.serialno());
        info.init();
        comment.init();

        if(streamState.pagein(page) < 0)
        {
            throw new IOException("Problem with first Ogg header page!");
        }

        if(streamState.packetout(packet) != 1)
        {
            throw new IOException("Problem with first Ogg header packet!");
        }

        if(info.synthesis_headerin(comment, packet) < 0)
        {
            throw new IOException("File does not contain Vorbis header!");
        }

        int i = 0;
        while(i < 2)
        {
            while(i < 2)
            {
                int result = syncState.pageout(page);
                if(result == 0) break;
                if(result == 1)
                {
                    streamState.pagein(page);
                    while(i < 2)
                    {
                        result = streamState.packetout(packet);
                        if(result == 0) break;

                        if(result == -1)
                        {
                            throw new IOException("Secondary Ogg header is corrupted!");
                        }

                        info.synthesis_headerin(comment, packet);
                        i++ ;
                    }
                }
            }
            currentIndex = syncState.buffer(bufferSize);
            bytes = input.read(syncState.data, currentIndex, bufferSize);
            if(bytes < 0) bytes = 0;
            if(bytes == 0 && i < 2)
            {
                throw new IOException("End of file reached before finished reading Ogg header");
            }

            syncState.wrote(bytes);
        }

        currentIndex = syncState.buffer(bufferSize);
        buffer = syncState.data;

        return true;
    }

    @Override
    public boolean initialized()
    {
        return initialized;
    }

    @Override
    public void cleanup() throws IOException
    {
        streamState.clear();
        block.clear();
        dspState.clear();
        info.clear();
        syncState.clear();

        try
        {
            input.close();
        }
        catch(IOException ioe)
        {
        }

        streamState = null;
        block = null;
        dspState = null;
        info = null;
        syncState = null;
        input = null;
    }

    @Override
    public AudioBuffer read(int n) throws IOException
    {
        byte[] result = null;
        while(!eof && (result == null || result.length < 131072))
        {
            if(result == null)
                result = readBuffer();
            else
                result = Buffers.merge(result, readBuffer());
        }
        if(result == null) return null;
        return new AudioBuffer(result, audioFormat);
    }

    private byte[] readBuffer() throws IOException
    {
        if(!initialized)
        {
            throw new IOException("Codec is not initialized yet!");
        }

        if(eof)
        {
            throw new EOFException("Codec has reached end of file");
        }

        if(convertedBuffer == null) convertedBuffer = new byte[convertedSize];
        byte[] data = null;

        float[][] pcmf;

        int samples, bout, ptr, mono, value, i, j;

        switch(syncState.pageout(page))
        {
            case 0:
            case -1:
                break;

            default:
                streamState.pagein(page);
                if(page.granulepos() == 0) // We have reached end of file
                {
                    eof = true;
                    return null;
                }

                packets: while(true)
                {
                    switch(streamState.packetout(packet))
                    {
                        case 0:
                            break packets;
                        case -1:
                            break;

                        default:
                            if(block.synthesis(packet) == 0) dspState.synthesis_blockin(block);
                            while((samples = dspState.synthesis_pcmout(pcmData, pcmIndex)) > 0)
                            {
                                pcmf = pcmData[0];
                                bout = (samples < convertedSize ? samples : convertedSize);
                                for(i = 0; i < info.channels; i++ )
                                {
                                    ptr = i * 2;
                                    mono = pcmIndex[i];
                                    for(j = 0; j < bout; j++ )
                                    {
                                        value = (int)(pcmf[i][mono + j] * 32767.);
                                        if(value > Short.MAX_VALUE) value = Short.MAX_VALUE;
                                        if(value < Short.MIN_VALUE) value = Short.MIN_VALUE;
                                        if(value < 0) value = value | 0x8000;
                                        convertedBuffer[ptr] = (byte)(value);
                                        convertedBuffer[ptr + 1] = (byte)(value >>> 8);
                                        ptr += 2 * (info.channels);
                                    }
                                }
                                dspState.synthesis_read(bout);

                                data = Buffers.merge(data, convertedBuffer, 2 * info.channels * bout);
                            }
                            break;
                    }
                }

                if(page.eos() != 0) // We reached the end of the stream
                {
                    eof = true;
                }
        }

        if(!eof)
        {
            currentIndex = syncState.buffer(bufferSize);
            buffer = syncState.data;
            try
            {
                bufferLength = input.read(buffer, currentIndex, bufferSize);
            }
            catch(Exception e)
            {
                throw new IOException("Could not read buffer", e);
            }
            if(bufferLength == -1)
            {
                return data;
            }

            syncState.wrote(bufferLength);
            if(bufferLength == 0) eof = true;
        }

        return data;
    }

    @Override
    public AudioBuffer readAll() throws IOException
    {
        byte[] result = null;
        while(!eof)
        {
            result = Buffers.merge(result, readBuffer());
        }
        return new AudioBuffer(result, audioFormat);
    }

    @Override
    public AudioFormat getAudioFormat()
    {
        return audioFormat;
    }

    @Override
    public void setAudioFormat(AudioFormat format)
    {
        audioFormat = format;
    }

    @Override
    public void update(int buffersProcessed)
    {
        buffers -= buffersProcessed;
        if(buffers == 0 && eof)
        {
            channel.stop();
        }
        if(buffers < 2)
        {
            try
            {
                eof = prepareBuffers(2);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean prepareBuffers(int n) throws IOException
    {
        if(eof)
        {
            return true;
        }
        buffers++ ;
        AudioBuffer buffer = read(n);
        if(buffer == null) return true;
        ByteBuffer audioBuffer = buffer.toByteBuffer();

        int bufferPointer = AL10.alGenBuffers();
        AL10.alBufferData(bufferPointer, channel.getFormat(), audioBuffer, channel.getSampleRate());

        AL10.alSourceQueueBuffers(channel.getSource(0), bufferPointer);
        return eof;
    }

}
