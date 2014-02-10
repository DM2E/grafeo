package eu.dm2e.grafeo.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 * <p/>
 * Author: Kai Eckert, Konstantin Baierer
 */
public class TestResettableInputStream extends TestCase
{
    @Test
    public void testReset() throws Exception
    {
        InputStream input = new ByteArrayInputStream(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 });
        InputStream resettable = new ResettableInputStream(input);

        assertTrue(resettable.markSupported());
        resettable.mark(4);

        byte[] peekBuffer = new byte[4];
        int peekBytesRead = resettable.read(peekBuffer, 0, 4);
        assertEquals(4, peekBytesRead);
        for (int i=0; i<4; i++)
            assertEquals(i, peekBuffer[i]);

        resettable.reset();

        byte[] completeBuffer = new byte[8];
        int bytesRead = resettable.read(completeBuffer, 0, 8);
        assertEquals(8, bytesRead);
        for (int i=0; i<8; i++)
            assertEquals(i, completeBuffer[i]);
    }


    @Test
    public void testResetTwice() throws Exception
    {
        InputStream input = new ByteArrayInputStream(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7 });
        InputStream resettable = new ResettableInputStream(input);

        resettable.mark(2);
        resettable.read(new byte[2], 0, 2); // discard

        resettable.reset();
        resettable.mark(4);

        byte[] peekBuffer = new byte[4];
        int peekBytesRead = resettable.read(peekBuffer, 0, 4);
        assertEquals(4, peekBytesRead);
        for (int i=0; i<4; i++)
            assertEquals(i, peekBuffer[i]);

        resettable.reset();

        byte[] completeBuffer = new byte[8];
        int bytesRead = resettable.read(completeBuffer, 0, 8);
        assertEquals(8, bytesRead);
        for (int i=0; i<8; i++)
            assertEquals(i, completeBuffer[i]);
    }
}
