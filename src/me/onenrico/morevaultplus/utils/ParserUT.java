//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserUT {
	public static List<HashMap<String, String>> extract(final String name, final String text) {
		final List<HashMap<String, String>> result = new ArrayList<>();
		final HashMap<String, String> value = new HashMap<>();
		String[] split;
		for (int length = (split = text.split("<" + name + ">")).length, i = 0; i < length; ++i) {
			final String raw = split[i];
			if (!raw.isEmpty()) {
				final String inside = raw.split("</" + name + ">")[0];
				value.clear();
				String[] split2;
				for (int length2 = (split2 = inside.split("<>")).length, j = 0; j < length2; ++j) {
					final String v = split2[j];
					if (v.split("=").length > 1) {
						final String left = v.split("=")[0].toLowerCase();
						final String right = v.split("=")[1];
						value.put(left, right);
					}
				}
				result.add(value);
			}
		}
		return result;
	}
}
