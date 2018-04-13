/*
 * ARMThumbOptions
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.grt.filemanager.model.imp.local.archive.inter.org.tukaani.xz;

import com.grt.filemanager.model.imp.local.archive.inter.org.tukaani.xz.simple.ARMThumb;

import java.io.InputStream;

/**
 * BCJ filter for little endian ARM-Thumb instructions.
 */
public class ARMThumbOptions extends BCJOptions {
    private static final int ALIGNMENT = 2;

    public ARMThumbOptions() {
        super(ALIGNMENT);
    }

    public FinishableOutputStream getOutputStream(FinishableOutputStream out) {
        return new SimpleOutputStream(out, new ARMThumb(true, startOffset));
    }

    public InputStream getInputStream(InputStream in) {
        return new SimpleInputStream(in, new ARMThumb(false, startOffset));
    }

    FilterEncoder getFilterEncoder() {
        return new BCJEncoder(this, BCJCoder.ARMTHUMB_FILTER_ID);
    }
}
