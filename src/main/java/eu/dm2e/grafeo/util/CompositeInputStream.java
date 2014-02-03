package eu.dm2e.grafeo.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 * <p/>
 * Author: Kai Eckert, Konstantin Baierer
 */
public class CompositeInputStream extends InputStream
{
    private final InputStream first;
    private final InputStream second;

    public CompositeInputStream(InputStream first, InputStream second)
    {
        this.first = first;
        this.second = second;
    }

    public int read() throws IOException
    {
        int result = first.read();
        if (result == -1)
            result = second.read();
        return result;
    }

    @Override
    public void close() throws IOException {
        first.close();
        second.close();
        super.close();
    }
}
