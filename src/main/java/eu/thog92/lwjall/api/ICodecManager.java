package eu.thog92.lwjall.api;

public interface ICodecManager
{
    /**
     * Registers a new codec for given type
     * 
     * @param type
     *            The type identifier of the codec (ie. "wav")
     * @param codecClass
     *            The {@link ICodec}'s class
     */
    void registerCodec(String type, Class<? extends ICodec> codecClass);

    /**
     * Creates a new {@link ICodec} based on given type
     * 
     * @param type
     *            The type identifier of the codec (ie. "wav")
     * @return
     *         A corresponding {@link ICodec}, if any found and <code>null</code> if none found
     */
    ICodec getCodec(String type);
}
