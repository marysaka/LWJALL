package eu.thog92.lwjall.api;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface IChannel
{

    int getSampleRate();

    void setSampleRate(int sampleRate);

    int getSource(int index);

    void cleanup();

    void setAudioFormat(AudioFormat audioFormat);

    void play();

    void pause();

    void stop();

    void rewind();

    boolean isPlaying();

    float getPlayingDuration();

    void setup(AudioFormat audioFormat, ByteBuffer buffer) throws Exception;

    boolean hasStopped();

    float getGain();

    void setGain(float gain);

    void setVelocity(FloatBuffer velocity);

    void setPosition(FloatBuffer pos);

    int getFormat();
}
