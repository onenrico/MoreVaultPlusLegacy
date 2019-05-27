//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.nbt;

import com.google.gson.Gson;

public class GsonWrapper {
	private static final Gson gson;

	static {
		gson = new Gson();
	}

	public static String getString(final Object obj) {
		return GsonWrapper.gson.toJson(obj);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T deserializeJson(final String json, final Class<T> type) {
		try {
			if (json == null) {
				return null;
			}
			final T obj = (T) GsonWrapper.gson.fromJson(json, (Class) type);
			return type.cast(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
