package eu.thog92.lwjall.test;

import eu.thog92.lwjall.ALSoundProvider;
import org.lwjgl.LWJGLException;

public class Main
{
    public static void main(String[] args) throws LWJGLException
    {
        ALSoundProvider soundProvider = new ALSoundProvider();

        soundProvider.cleanUp();
    }
}
