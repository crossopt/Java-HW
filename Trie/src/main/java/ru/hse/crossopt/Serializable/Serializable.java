package ru.hse.crossopt.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Custom interface for serialization of objects. */
public interface Serializable {
    /**
     * Writes object into output stream.
     * @param out an output stream to write into.
     * @throws IOException if writing into stream failed.
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Reads object from input stream.
     * @param in An input stream to read from.
     * @throws IOException if reading from stream failed.
     */
    void deserialize(InputStream in) throws IOException;
}
