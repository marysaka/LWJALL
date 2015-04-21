package eu.thog92.lwjall.tests;

import eu.thog92.lwjall.ALSoundProvider;
import eu.thog92.lwjall.api.AbstractSource;
import eu.thog92.lwjall.util.LWJALLException;

public class TestReading {

    public static void main(String[] args) throws LWJALLException {
        ALSoundProvider provider = new ALSoundProvider();
        provider.setMasterGain(0.1f);
        boolean streaming = true;
        AbstractSource source = provider.newSource("test", TestReading.class.getResource("/sounds/HangingOn.ogg"), streaming);
        source.play();
        while(source.isPlaying()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            provider.update();
        }

        provider.cleanup();
    }

}
