/*
/*
 * @(#)ServiceRegistry.java	1.24 06/06/28
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.grt.filemanager.model.imp.local.archive.inter.unrar;

import java.util.Iterator;

import com.grt.filemanager.model.imp.local.archive.inter.sun.misc.Service;

public class ServiceRegistry {

	public static <T> Iterator<T> lookupProviders(Class<T> providerClass,
			ClassLoader loader) {
		if (providerClass == null) {
			throw new IllegalArgumentException("providerClass == null!");
		}
		return Service.providers(providerClass, loader);
	}

}
