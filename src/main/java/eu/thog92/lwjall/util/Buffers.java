package eu.thog92.lwjall.util;

import org.lwjgl.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Utility class performing operations on stream and {@link ByteBuffer}
 */
public final class Buffers
{

    /**
     * Reads the entire stream and stores it in a {@link ByteBuffer}
     * 
     * @param stream
     *            The {@link InputStream} to read
     * @return
     *         A {@link ByteBuffer} containing the data from the stream
     * @throws IOException
     *             Thrown if anything wrong happens while reading the stream
     */
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

    /**
     * Creates a flipped {@link ByteBuffer} from given byte array
     * 
     * @param byteArray
     *            The array to store in a {@link ByteBuffer}
     * @return
     *         A {@link ByteBuffer} containing the data from given array
     * @see {@link ByteBuffer#flip()}
     */
    public static ByteBuffer flippedByteBuffer(byte[] byteArray)
    {
        ByteBuffer buffer = BufferUtils.createByteBuffer(byteArray.length);
        buffer.put(byteArray);
        buffer.flip();
        return buffer;
    }

    /**
     * Merges two arrays into one
     * 
     * @param arrays
     *            The arrays to merge
     * @return
     *         An array containing all the data from given arrays or null if no arrays to given
     */
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

    /**
     * Merges two arrays into one
     * 
     * @param arrayOne
     *            The first array
     * @param arrayTwo
     *            The second array
     * @param arrayTwoBytes
     *            The number of bytes to add from the second array
     * @return
     *         A merged array whose length is <code>arrayOne.length + arrayTwoBytes</code>
     */
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

    /**
     * Creates a new {@link FloatBuffer}.
     * 
     * @param size
     *            The number of elements
     * @return
     *         A new {@link FloatBuffer}
     */
    public static FloatBuffer createFloatBuffer(int size)
    {
        return ByteBuffer.allocateDirect(size << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
}
