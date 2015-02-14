package eu.thog92.lwjall.util;

import org.lwjgl.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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

    public static byte[] merge(byte[]... arrays)
    {
        if(arrays == null || arrays.length == 0) return null;
        byte[] result = arrays[0];
        for(int i = 1; i < arrays.length; i++ )
        {
            result = merge(result, arrays[i], arrays[i].length);
        }
        return result;
    }

    public static byte[] merge(byte[] arrayOne, byte[] arrayTwo, int arrayTwoBytes)
    {
        int bytes = arrayTwoBytes;

        // Make sure we aren't trying to append more than is there:
        if(arrayTwo == null || arrayTwo.length == 0)
            bytes = 0;
        else if(arrayTwo.length < arrayTwoBytes) bytes = arrayTwo.length;

        if(arrayOne == null && (arrayTwo == null || bytes <= 0))
            return null;
        else if(arrayOne == null)
        {
            // create the new array, same length as arrayTwo:
            byte[] result = new byte[bytes];
            // fill the new array with the contents of arrayTwo:
            System.arraycopy(arrayTwo, 0, result, 0, bytes);
            return result;
        }
        else if(arrayTwo == null || bytes <= 0)
        {
            // create the new array, same length as arrayOne:
            byte[] result = new byte[arrayOne.length];
            // fill the new array with the contents of arrayOne:
            System.arraycopy(arrayOne, 0, result, 0, arrayOne.length);
            return result;
        }
        else
        {
            // create the new array large enough to hold both arrays:
            byte[] result = new byte[arrayOne.length + bytes];
            System.arraycopy(arrayOne, 0, result, 0, arrayOne.length);
            // fill the new array with the contents of both arrays:
            System.arraycopy(arrayTwo, 0, result, arrayOne.length, bytes);
            return result;
        }
    }

    public static FloatBuffer createFloatBuffer(int size) {
        return ByteBuffer.allocateDirect(size << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
}
