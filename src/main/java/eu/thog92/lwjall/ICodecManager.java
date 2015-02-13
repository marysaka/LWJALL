package eu.thog92.lwjall;

/**
 * Created by Thog92 on 13/02/2015.
 */
public interface ICodecManager
{
    void registerCodec(String type, Class<?> codecClass);

    ICodec getCodec(String type);
}
