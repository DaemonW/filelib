package com.daemonw.filelib.model;

import java.io.IOException;

public interface Seekable {

    void seekTo(long offset) throws IOException;

    void write(byte[] buffer) throws IOException;

    void write(byte[] buffer, int offset, int length) throws IOException;
}
