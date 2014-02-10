package eu.dm2e.grafeo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This file was created within the DM2E project.
 * http://dm2e.eu
 * http://github.com/dm2e
 * <p/>
 * Author: Kai Eckert, Konstantin Baierer
 */
public class ResettableInputStream extends InputStream
{
    private InputStream source;
    private boolean marked;
    private ByteArrayOutputStream savedBytes;
    private boolean keepOpen = false;

    public ResettableInputStream(InputStream source)
    {
        this.source = source;
    }

    public ResettableInputStream(InputStream source, boolean keepOpen)
    {
        this.source = source;
        this.keepOpen = keepOpen;
    }

    public boolean markSupported()
    {
        return true;
    }

    public synchronized void mark(int readlimit)
    {
        savedBytes = new ByteArrayOutputStream(readlimit);
        marked = true;
    }

    public synchronized void reset() throws IOException
    {
        if (!marked)
            throw new IOException("Cannot reset unmarked stream");
        source = new CompositeInputStream(new ByteArrayInputStream(savedBytes.toByteArray()), source);
        marked = false;
    }

    public synchronized int read() throws IOException
    {
        int result = source.read();
        if (marked)
            savedBytes.write(result);
        return result;
    }

    @Override
    public void close() throws IOException {
        if (!keepOpen || !marked) {
            source.close();
            super.close();
        }
    }

    public void unmark() {
        marked = false;
    }

    public void closeActually() throws IOException {
        keepOpen = false;
        close();
    }
}
