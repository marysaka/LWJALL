package eu.thog92.lwjall;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thog92 on 13/02/2015.
 */
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
