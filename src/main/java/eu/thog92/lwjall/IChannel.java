package eu.thog92.lwjall;

import javax.sound.sampled.AudioFormat;

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
}
