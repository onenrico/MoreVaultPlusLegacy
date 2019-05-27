//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.callable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class PlaceholderCall implements Callable<String> {
	public HashMap<String, Integer> cacheIndex;
	public String current;
	public List<String> values;
	Random r;

	public PlaceholderCall() {
		cacheIndex = new HashMap<>();
		current = "";
		values = new ArrayList<>();
		r = new Random();
	}

	@Override
	public String call() throws Exception {
		if (values.isEmpty()) {
			return "";
		}
		int index = 0;
		if (current.isEmpty()) {
			index = r.nextInt(values.size());
		} else {
			index = cacheIndex.getOrDefault(current, 0);
		}
		if (index == values.size()) {
			index = 0;
		}
		final String result = values.get(index++);
		cacheIndex.put(current, index);
		return result;
	}

	public String callFrom(final String key) {
		current = key;
		String result = "";
		try {
			result = call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		current = "";
		return result;
	}
}
