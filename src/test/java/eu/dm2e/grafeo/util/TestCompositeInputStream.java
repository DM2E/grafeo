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

public class TestCompositeInputStream extends TestCase
{
    @Test
    public void testRead() throws Exception
    {
        InputStream first = new ByteArrayInputStream(new byte[]{ 0, 1, 2, 3 });
        InputStream second = new ByteArrayInputStream(new byte[]{ 4, 5, 6, 7 });
        InputStream composite = new CompositeInputStream(first, second);

        byte[] buffer = new byte[8];
        int bytesRead = composite.read(buffer, 0, 8);
        assertEquals(8, bytesRead);
        for (int i = 0; i < 8; i++)
            assertEquals(i, buffer[i]);
    }
}
