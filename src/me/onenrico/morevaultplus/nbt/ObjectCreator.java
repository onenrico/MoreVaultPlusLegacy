//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import java.lang.reflect.Constructor;

public enum ObjectCreator {
	NMS_NBTTAGCOMPOUND(ClassWrapper.NMS_NBTTAGCOMPOUND.getClazz(), new Class[0]), NMS_BLOCKPOSITION(
			ClassWrapper.NMS_BLOCKPOSITION.getClazz(), new Class[] { Integer.TYPE, Integer.TYPE, Integer.TYPE });

	private Constructor<?> construct;

	private ObjectCreator(final Class<?> clazz, final Class<?>[] args) {
		try {
			construct = clazz.getConstructor(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Object getInstance(final Object... args) {
		try {
			return construct.newInstance(args);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
