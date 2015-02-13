package eu.thog92.lwjall.util;

import org.lwjgl.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Buffers
{

    public static ByteBuffer consumeStream(InputStream stream) throws IOException
    {
        int i;
        byte[] buffer = new byte[4096];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while((i = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, i);
        }
        baos.flush();
        baos.close();
        return flippedByteBuffer(baos.toByteArray());
    }

    public static ByteBuffer flippedByteBuffer(byte[] byteArray)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(byteArray.length);
        buffer.put(byteArray);
        buffer.flip();
        return buffer;
    }
}
