package eu.thog92.lwjall;

import eu.thog92.lwjall.ICodec;

/**
 * Created by Thog92 on 13/02/2015.
 */
public interface ICodecManager
{
    void registerCodec(String type, Class<?> codecClass);

    ICodec getCodec(String type);
}
