package eu.thog92.lwjall.internal;

import eu.thog92.lwjall.api.ICodec;
import eu.thog92.lwjall.api.ICodecManager;

import java.util.HashMap;
import java.util.Map;

public class ALCodecManager implements ICodecManager
{
    private final Map<String, Class> supportedCodecs = new HashMap<String, Class>();

    @Override
    public void registerCodec(String type, Class<?> codecClass)
    {
        if(supportedCodecs.containsKey(type))
        {
            System.err.println();
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
