package eu.thog92.lwjall;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.LWJGLException;

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

	void setup(AudioFormat audioFormat, ByteBuffer buffer) throws LWJGLException;

	boolean hasStopped();

	void setGain(float gain);

	float getGain();

	void setVelocity(FloatBuffer velocity);

	void setPosition(FloatBuffer pos);
}
