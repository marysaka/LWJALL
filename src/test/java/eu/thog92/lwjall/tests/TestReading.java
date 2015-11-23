package eu.thog92.lwjall.tests;

import com.badlogic.SharedLibraryLoader;
import eu.thog92.lwjall.ALSoundProvider;
import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.util.LWJALLException;

public class TestReading {

    public static void main(String[] args) throws LWJALLException {
        SharedLibraryLoader.load();
        ALSoundProvider provider = new ALSoundProvider();
        boolean streaming = true;
        AbstractSource source = provider.newSource("test", TestReading.class.getResource("/sounds/test.wav"), streaming);
        source.play();
        while(source.isPlaying()) {
            provider.setMasterGain(1.0f);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            provider.update();
        }

        provider.cleanup();
    }

}
