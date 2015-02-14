package eu.thog92.lwjall.api;

public interface ICodecManager
{
    void registerCodec(String type, Class<?> codecClass);

    ICodec getCodec(String type);
}
