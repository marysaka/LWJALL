package eu.thog92.lwjall.internal;

import java.util.HashMap;
import java.util.Map;

import eu.thog92.lwjall.api.ICodec;
import eu.thog92.lwjall.api.ICodecManager;

public class ALCodecManager implements ICodecManager
{
    private final Map<String, Class<? extends ICodec>> supportedCodecs = new HashMap<String, Class<? extends ICodec>>();

    @Override
    public void registerCodec(String type, Class<? extends ICodec> codecClass)
    {
        if(supportedCodecs.containsKey(type))
        {
            System.err.println("Tried to register"); // TODO: True logger
            return;
        }
        supportedCodecs.put(type, codecClass);
    }

    @Override
    public ICodec getCodec(String type)
    {
        try
        {
            if(supportedCodecs.containsKey(type))
            {
                return (ICodec) supportedCodecs.get(type).newInstance();
            }
        }
        catch(InstantiationException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
