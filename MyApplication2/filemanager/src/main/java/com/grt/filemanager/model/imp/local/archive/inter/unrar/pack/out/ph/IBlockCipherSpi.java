package com.grt.filemanager.model.imp.local.archive.inter.unrar.pack.out.ph;

import java.security.InvalidKeyException;
import java.util.Iterator;

interface IBlockCipherSpi extends Cloneable {

	Iterator blockSizes();

	Iterator keySizes();

	Object makeKey(byte[] k, int bs) throws InvalidKeyException;

	void encrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object k,
                 int bs);

	void decrypt(byte[] in, int inOffset, byte[] out, int outOffset, Object k,
                 int bs);

	boolean selfTest();
}
